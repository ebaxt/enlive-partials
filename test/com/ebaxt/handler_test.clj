(ns com.ebaxt.handler-test
  (:use [ring.adapter.jetty]
        [com.ebaxt.enlive-partials]))


(def template-handler (handle-partials
                       (fn [req] {:status 200
                                 :headers {}
                                 :body "<a href='/templates/foo/example.html'>Try me</a></br ><a href='/templates/foo/example_vars.html'>Try me with vars</a>"})
                       "templates/test/app" {:template-context "templates"
                                             :vars {:foo "This string replaced ${foo}"
                                                    :bar "test"}}))
