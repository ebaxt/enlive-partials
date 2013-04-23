(ns com.ebaxt.enlive-partials
  (:require [ring.util.codec :as codec]
            [ring.util.response :as response]
            [clojure.java.io :as io])
  (:use net.cgrand.enlive-html)
  (:import java.io.File)
  (:use clojure.pprint)
  (:import java.lang.Thread)
  (:import java.lang.RuntimeException))

(def ^:dynamic *template-path*)

(defn render
  "Given a seq of Enlive nodes, return the corresponding HTML string."
  [t]
  (apply str (emit* t)))

(declare construct-html)

(defn- find-html-resource [file]
  (let [file (.replaceAll file "^/" "")
        path (str *template-path* "/" file)
        resource (io/resource path)]
    (if resource
      (html-resource resource)
      (throw (Exception. (format "Template doesn't exist: %s" path))))))

(defn- html-body [name]
  (:content (first (select (find-html-resource name) [:body]))))

(defn- include-html [h]
  (let [includes (select h [:_include])]
    (loop [h h
           includes (seq includes)]
      (if includes
        (let [file (-> (first includes) :attrs :file)
              include (construct-html (html-body file))]
          (recur (transform h [[:_include (attr= :file file)]] (substitute include))
                 (next includes)))
        h))))

(defn- maps [c] (filter map? c))

(defn- replace-html [h c]
  (let [id (-> c :attrs :id)
        tag (:tag c)
        selector (keyword (str (name tag) "#" id))]
    (transform h [selector] (substitute c))))

(defn- wrap-html [h]
  (let [within (seq (select h [:_within]))]
    (if within
      (let [file (-> (first within) :attrs :file)
            outer (construct-html (find-html-resource file))
            content (maps (:content (first within)))]
        (loop [outer outer
               content (seq content)]
          (if content
            (recur (replace-html outer (first content)) (next content))
            outer)))
      h)))

(defn construct-html
  "Process a seq of Enlive nodes looking for `_include` and `_within` tags.
  Occurrences of `_include` are replaced by the resource to which they
  refer. The contents of `_within` tags are inserted into the resource
  to which they refer. `_within` is always the top-level tag in a file.
  `_include` can appear anywhere. Files with `_include` can reference
  files which themselves contain `_include` or `_within` tags, to an
  arbitrary level of nesting.

  For more information, see '[Design and Templating][dt]' in the project
  wiki.

  Returns a seq of Enlive nodes.

  [dt]: https://github.com/brentonashworth/one/wiki/Design-and-templating"
  ([nodes] (wrap-html (include-html nodes)))
  ([nodes vars]
     (do
       (if (nil? vars)
         (construct-html nodes)
         (replace-vars (construct-html nodes) vars)))))

(defn load-html
  "Accept a file (a path to a resource on the classpath) and return a
  HTML string processed per construct-html."
  [file]
  (render (construct-html (find-html-resource file))))

(defn- ensure-dir
  "Ensures that a directory exists at the given path, throwing if one does not."
  [^String path]
  (let [file (-> (io/resource path)
                 (io/as-file))]
    (if-not (and file (.isDirectory file) (.exists file))
      (throw (Exception. (format "Directory does not exist: %s" path))))))

(defn to-template-path [req opts]
  (let [{:keys [uri request-method context] :or {context ""}} req
        {:keys [template-path template-context]} opts]
    (if (and (= :get request-method)
             (re-matches (re-pattern (str "^" context "/" template-context "/.+")) uri)) 
      (let [no-context (.replaceFirst (codec/url-decode uri) (str context "/") "")]
        (.replaceFirst no-context template-context template-path)))))

(defn handle-partials
  "Adds support for enlive paritals"
  [handler ^String template-path & [opts]]
  (ensure-dir template-path)
  (let [opts (merge {:template-context template-path :template-path template-path} opts)]
    (fn [req]
      (if-let [path (to-template-path req opts)]
        (binding [*template-path* template-path]
          (let [resp (response/resource-response path)
                {:keys [headers body] :as response} resp]
            (if (and (= (type body) File)
                     (.endsWith (.getName body) ".html"))
              (let [new-body (render (construct-html (html-snippet (slurp body)) (:vars opts)))]
                {:status 200
                 :headers {"Content-Type" "text/html; charset=utf-8"}
                 :body new-body})
              resp)))
        (handler req)))))

