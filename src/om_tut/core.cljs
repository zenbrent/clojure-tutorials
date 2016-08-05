(ns om-tut.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

;; Current position:
;; https://github.com/omcljs/om/wiki/Basic-Tutorial#enhancing-your-first-om-component

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
;; It must consist of associative data structures -- map or indexed sequential
;; data like a vector, but NOT a set, list, or lazy sequence.
(defonce app-state
  (atom
    {:contacts
     [{:first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
      {:first "Alyssa" :middle-initial "P" :last "Hacker" :email "aphacker@mit.edu"}
      {:first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
      {:first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
      {:first "Cy" :middle-initial "D" :last "Effect" :email "bugs@mit.edu"}
      {:first "Lem" :middle-initial "E" :last "Tweakit" :email "morebugs@mit.edu"}]})) 

(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last ", " first (middle-name contact)))

(defn contact-view [contact owner]
  (reify
    om/IRender
    (render [this]
      (dom/li #js {:style #js {:backgroundColor "red"}}
              (dom/span nil (display-name contact))
              (dom/button nil "Delete")))))

(defn contacts-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
               (dom/h2 nil "Contact List")
               (apply dom/ul nil
                      (om/build-all contact-view (:contacts data)))))))


(defn reload-page 
  "Refreshes the page"
  []
  (.reload (.-location js/window)))

(om/root contacts-view app-state
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
