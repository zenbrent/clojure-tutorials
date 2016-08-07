(ns ^:figwheel-always om-tut.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :as string]))

;; https://github.com/omcljs/om/wiki/Basic-Tutorial#enhancing-your-first-om-component

;; lighttable notes!
;; <C-space> pulls up the command pane. sups useful.

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

(defn parse-contact [contact-str]
  (let [[first middle last :as parts] (string/split contact-str #"\s+")
        [first last middle] (if (nil? last) [first middle] [first last middle])
        middle (when middle (string/replace middle "." "")) ;; e.g. Brent M. Brimhall
        c (if middle (count middle) 0)]
    (when (>= (count parts) 2)
      (cond-> {:first first :last last}
        (== c 1) (assoc :middle-initial middle)
        (>= c 2) (assoc :middle middle))))) 

(defn add-contact [data owner]
  (let [new-contact (-> (om/get-node owner "new-contact")
                        .-value
                        parse-contact)]
    (when new-contact
      (om/transact! data :contacts #(conj % new-contact)))))

(defn contact-view [contact owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/li nil
              (dom/span nil (display-name contact))
              (dom/button #js {:onClick (fn [e] (put! delete @contact))} "Delete")))))

(defn contacts-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:delete (chan)})
    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
              (let [contact (<! delete)]
                (om/transact! data :contacts
                              ;; using vec to transform the result of a lazy sequence to a vector
                              ;; because state can only consist of associative data like maps & vectors
                              (fn [xs] (vec (remove #(= contact %) xs))))
                (recur))))))
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/h2 nil "Contact List")
               (apply dom/ul nil
                      (om/build-all contact-view (:contacts data)
                                    {:init-state state}))
               (dom/div nil
                        (dom/input #js {:type "text" :ref "new-contact"})
                        (dom/button #js {:onClick #(add-contact data owner)} "Add contact"))))))


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
