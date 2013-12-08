(ns clojurescript-goog-fx-dragdrop.dragdrop
  (:require [domina :as dom]
            [domina.events :as ev]
            [domina.css :as css]
            [goog.fx.DragDrop :as dragdrop]
            [goog.fx.DragDropGroup :as dragdropgroup])
  (:use-macros [clojurescript-goog-fx-dragdrop.macros :only [goog-extend]]
               [cljs.core :only [this-as]]))

(defn get-event [event]
  (.-evt event))

(defn drag-over [event]
  (dom/set-style! (.. (get-event event) -dropTargetItem -element) :background "red"))

(defn drag-out [event]
  (dom/set-style! (.. (get-event event) -dropTargetItem -element) :background "silver"))

(defn dropp [event]
  (let [evt (get-event event)]
    (dom/set-style! (.. evt -dropTargetItem -element) :background "silver")
    (let [log-str [(.-data (.-dragSourceItem evt)) " dropped onto "
                   (.-data (.-dropTargetItem evt)) " at "
                   (.-viewportX evt) "x"
                   (.-viewportY evt)]]
      (dom/log log-str))))

(defn drop-list-1 [event]
  (let [evt (get-event event)]
    (dom/set-style! (.. evt -dropTargetItem -element) :background "silver")
    (let [log-str [(.. evt -dragSourceItem -data) " dropped onto "
                   (.. evt -dropTargetItem -data) " in list 1."]]
      (dom/log log-str))))

(defn drag-list-1 [event]
  (let [log-str [(.. (get-event event) -dragSourceItem -data) " dragged from list 1"]]
    (dom/log log-str)))

(defn drag-start [event]
  (dom/set-style! (.. (get-event event) -dragSourceItem -element) :opacity 0.5))

(defn drag-end [event]
  (dom/set-style! (.. (get-event event) -dragSourceItem -element) :opacity 1.0))

(goog-extend FooDrag goog/fx.DragDrop
             ([element opt-data]
              (this-as this
                       (goog/fx.DragDrop.call this element opt-data)))
             (createDragElement [source-el]
                                  (dom/log "goog.dom.createDom('div', 'foo', 'Custom drag element')")
                                  (goog/dom.createDom "div" "foo" "Custon drag element"))

             (getDragElementPosition [source-el, el, event]
                                        (dom/log "new goog.math.Coordinate(event.clientX, event.clientY)")
                                        (goog/math.Coordinate. (.-clientX event) (.-clientY event))))

(defn ^:export init []
  (when (and js/document
             (.-getElementById js/document))
    (let [button1 (FooDrag. (dom/by-id "button1"))
          button2 (goog.fx.DragDrop. "button2" {:opt "button 2"})
          list1 (goog.fx.DragDropGroup.)
          list2 (goog.fx.DragDropGroup.)
          nodes1 (.-childNodes (dom/by-id "list1"))
          nodes2 (.-childNodes (dom/by-id "list2"))]
      (doseq [el nodes1]
        (if (and (= 1 (.-nodeType el)) (= "LI" (.-nodeName el)))
          (.addItem list1 el (.-nodeValue (.-firstChild el)))))

      (doseq [el nodes2]
        (if (and (= 1 (.-nodeType el)) (= "LI" (.-nodeName el)))
          (.addItem list2 el (.-nodeValue (.-firstChild el)))))

      (.addTarget list1 button1)
      (.addTarget list1 button2)
      (.addTarget list1 list1)

      (.addTarget list2 button2)
      (.addTarget list2 list1)

      (.addTarget button1 list1)

      (.setSourceClass button1 "source")
      (.setSourceClass button1 "target")
      (.setDragClass button1 "drag")
      (.setSourceClass button2 "source")
      (.setTargetClass button2 "target")
      (.setSourceClass list1 "source")
      (.setTargetClass list1 "target")
      (.setSourceClass list2 "source")

      (.init button1)
      (.init button2)
      (.init list1)
      (.init list2)

      (ev/listen! list1 :dragover drag-over)
      (ev/listen! list1 :dragout drag-out)
      (ev/listen! list1 :drop drop-list-1)
      (ev/listen! list1 :drag drag-list-1)
      (ev/listen! list1 :dragstart drag-start)
      (ev/listen! list1 :dragend drag-end)

      (ev/listen! list2 :dragover drag-over)
      (ev/listen! list2 :dragout drag-out)
      (ev/listen! list2 :drop dropp)
      (ev/listen! list2 :dragstart drag-start)
      (ev/listen! list2 :dragend drag-end)

      (ev/listen! button1 :dragover drag-over)
      (ev/listen! button1 :dragout drag-out)
      (ev/listen! button1 :drop dropp)
      (ev/listen! button1 :dragstart drag-start)
      (ev/listen! button1 :dragend drag-end)

      (ev/listen! button2 :dragover drag-over)
      (ev/listen! button2 :dragout drag-out)
      (ev/listen! button2 :drop dropp)

      (ev/listen! (dom/by-id "button1") :click (fn [evt] (dom/log "click"))))))
