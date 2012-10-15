(ns ring.middleware.enlive-partials
  (:require [ring.util.codec :as codec]
            [ring.util.response :as response])
  (:import java.io.File))

(defn- ensure-dir
  "Ensures that a directory exists at the given path, throwing if one does not."
  [^String dir-path]
  (let [dir (File. dir-path)]
    (if-not (.exists dir)
      (throw (Exception. (format "Directory does not exist: %s" dir-path))))))

(defn enlive-partials
  "Adds support for enlive paritals"
  [app ^String root-path & [opts]]
  (ensure-dir root-path)
  (let [opts (merge {:root root-path, :index-files? true, :allow-symlinks? false} opts)]
    (fn [req]
      (if-not (= :get (:request-method req))
        (app req)
        (let [path (.substring ^String (codec/url-decode (:uri req)) 1)]
          (println path)
          ;; (or (response/file-response path opts)
          ;;     (app req))
          ))))  
  )

