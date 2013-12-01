(ns clojurescript-goog-fx-dragdrop.macros
  (:require [cljs.compiler :as compiler]
            [cljs.core :as cljs]))

(defn- to-property [sym]
  (symbol (str "-" sym)))

; from: https://groups.google.com/forum/#!topic/clojure/DskvJVsiGFA
(defmacro goog-extend [type base-type ctor & methods]
  `(do
     (defn ~type ~@ctor)

     (goog/inherits ~type ~base-type)

     ~@(map
        (fn [method]
          `(set! (.. ~type -prototype ~(to-property (first method)))
                 (fn ~@(rest method))))
        methods)))
