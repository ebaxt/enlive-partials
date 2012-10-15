(defproject com.ebaxt.enlive-partials "0.1.0"
  :description "Support for including partial templates with enlive."
  :url "https://github.com/ebaxt/enlive-partials"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [enlive "1.0.1"]
                 [ring/ring-core "1.1.6"]]
  :profiles {
             :dev {:plugins [[lein-ring "0.7.5"]]
                   :dependencies [[ring-mock "0.1.3"]
                                  [ring/ring-jetty-adapter "1.1.6"]]
                   :ring {:handler com.ebaxt.handler-test/template-handler
                          :auto-reload? true
                          :auto-refresh? true}}})
