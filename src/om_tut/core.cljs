(ns om-tut.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
;; It must consist of associative data structures -- map or indexed sequential
;; data like a vector, but NOT a set, list, or lazy sequence.
(defonce app-state (atom {:list ["Lion" "Zebra" "Buffalo" "Antelope"]}))

(defn reload-page 
  "Refreshes the page"
  []
  (.reload (.-location js/window)))

(defn stripe [text bgc]
  (let [st #js {:backgroundColor bgc}]
    (dom/li #js {:style st} text)))

(om/root
  (fn [data owner]
    (om/component
      (apply dom/ul #js {:className "animals"} ; #js is a reader macro for making js objects or arrays -- it is shallow.
             (map stripe (:list data) (cycle ["#fbf" "#fcf"])))))
  app-state
  {:target (. js/document (getElementById "app-0"))})

; (om/root
;   (fn [data owner] ; takes app state data, and the backing react component.
;     (reify om/IRender ; must return an Om component. om.core/component macro generates om/IRenders.
;       (render [_]
;         (dom/div nil (:text data)))))
;   app-state
;   {:target (. js/document (getElementById "app-1"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
