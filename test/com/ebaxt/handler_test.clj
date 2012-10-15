(ns com.ebaxt.handler-test
  (:use [ring.adapter.jetty]
        [com.ebaxt.enlive-partials]))

(def template-handler (handle-partials
                       (fn [req] {:status 200 :headers {} :body "Whatup"})
                       "templates/test/app" {:template-context "templates"}))
