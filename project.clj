(defproject dependencies "0.0.1-SNAPSHOT"
  :description "clojure jar dependency finder and minifier"
  :main dependencies.core
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [asm "3.3.1"]
                 [asm/asm-util "3.3.1"]])