(ns cljdeps.core
  (:import (org.objectweb.asm ClassReader)
           (org.objectweb.asm.util TraceClassVisitor)
           (java.io FileInputStream PrintWriter StringWriter)))

    
; TODO: Use jar-jar links to minify --
; the command line arguments can be simulated:
; just "keep" the extra clojure classes as worked out
; by class-dep-names, below.

(defn class-name-from-var [ns-str var-str]
  (str (munge ns-str) "$" (munge var-str)))

(defn class-file-from-name [class-name]
  (str (.replace class-name "." "/") ".class"))

(defn read-class-codes [input-stream]
  (let [reader (ClassReader. input-stream)
        sw (StringWriter.)
        pw (PrintWriter. sw)
        visitor (TraceClassVisitor. pw)]
    (.accept reader visitor 0)
    (.flush pw)
    (.. sw getBuffer toString)))
  
(defn clojure-deps [class-codes]
  (let [x1 [#"(?m)LDC\s+\"(.+?)\"\s+LDC\s\"(.+?)\"\s+INVOKESTATIC clojure/lang/RT.var"
            #"(?m)LDC\s+\"(.+?)\"\s+LDC\s\"(.+?)\"\s+INVOKESTATIC clojure/lang/Symbol.create"]]
    (mapcat #(re-seq % class-codes) x1)))

(defn class-dep-names [file-path]
  (try
    (let [deps (-> file-path (FileInputStream.)
                   read-class-codes clojure-deps)]
      (map #(apply class-name-from-var (rest %)) deps))
    (catch Exception e)))
