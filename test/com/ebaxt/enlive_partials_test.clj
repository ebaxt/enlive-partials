(ns com.ebaxt.enlive-partials-test
  (:use com.ebaxt.enlive-partials
        clojure.test
        ring.mock.request
        net.cgrand.enlive-html))

(defn test-handler
  ([] (test-handler nil))
  ([vars] (handle-partials #()
                           "templates/test/app"
                           (merge {:template-context "design"} vars))))

(deftest test-enlive-partials
  (let [req ((test-handler) (request :get "/design/foo/example.html"))
        {:keys [status headers body]} req]
    (is (= 200 status))
    (is (= headers {"Content-Type" "text/html; charset=utf-8"}))
    (is (= '("Navigation Menu" "New Content" "A Footer") (select (html-snippet body) [:div content])))))

(deftest test-with-vars
  (let [req ((test-handler
              {:vars {:foo "FooWorks"
                      :bar "barclass"}})
             (request :get "/design/foo/example_vars.html"))
        {:keys [status headers body]} req]
    (is (= 200 status))
    (is (= headers {"Content-Type" "text/html; charset=utf-8"}))
    (is (= "FooWorks" (first  (select (html-snippet body) [:div#content content]))))
    (is (= "A Footer" (first  (select (html-snippet body) [:div.barclass content]))))))

(deftest test-with-vars-missing
  (is (thrown-with-msg?
       Exception
       #"Could not replace:"
       ((test-handler
         {:vars {:foo "FooWorks"}})
        (request :get "/design/foo/example_vars.html")))))

(deftest test-bad-template-dir
  (is (thrown-with-msg?
       Exception
       #"Directory does not exist: foo/test/not-here"
       (handle-partials #() "foo/test/not-here"))))

(deftest test-bad-partial
  (is (thrown-with-msg?
       Exception
       #"Template doesn't exist: templates/test/app/does_not_exist.html"
       ((test-handler) (request :get "/design/bad.html")))))


