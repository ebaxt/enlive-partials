(ns com.ebaxt.enlive-partials-test
  (:use com.ebaxt.enlive-partials
        clojure.test
        ring.mock.request
        net.cgrand.enlive-html))


(def ok-path-handler (handle-partials #() "templates/test/app" {:template-context "design"}))

(deftest test-enlive-partials
  (let [req (ok-path-handler (request :get "/design/foo/example.html"))
        {:keys [status headers body]} req]
    (is (= 200 status))
    (is (= headers {"Content-Type" "text/html; charset=utf-8"}))
    (is (= '("Navigation Menu" "New Content" "A Footer") (select (html-snippet body) [:div content])))))

(deftest test-bad-template-dir
  (is (thrown-with-msg?
        Exception
        #"Directory does not exist: foo/test/not-here"
        (handle-partials #() "foo/test/not-here"))))

(deftest test-bad-partial
  (is (thrown-with-msg?
        Exception
        #"Template doesn't exist: templates/test/app/does_not_exist.html"
        (ok-path-handler (request :get "/design/bad.html")))))

