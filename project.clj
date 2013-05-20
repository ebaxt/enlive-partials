(defproject com.ebaxt.enlive-partials "0.1.1"
  :min-lein-version "2.0.0"
  :description "Support for including partial templates with enlive."
  :url "https://github.com/ebaxt/enlive-partials"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [enlive "1.1.1"]
                 [ring/ring-core "1.1.8"]]
  :profiles {
             :dev {:plugins [[lein-ring "0.8.5"]
                             [lein-kibit "0.0.8"]]
                   :dependencies [[ring-mock "0.1.3"]
                                  [ring/ring-jetty-adapter "1.1.8"]]
                   :ring {:handler com.ebaxt.handler-test/template-handler
                          :auto-reload? true
                          :auto-refresh? true}}})
