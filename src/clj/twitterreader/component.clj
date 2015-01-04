(ns twitterreader.component
  (:require [clojure.core.async :as async :refer [go-loop <!]]
            [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]))

(defn read-tweets [tweet-chan read-active]
  (go-loop []
           (let [tweet (<! tweet-chan )]
             (println (:text tweet))
             (when @read-active (recur)))))

(defrecord TwiterReader [channels read-active]
  component/Lifecycle
  (start [component] (log/info "Starting Twitterreader component")
    (let [tweet-chan (:tweets channels)
          read-active (atom true)]
      (read-tweets tweet-chan read-active)
      (assoc component :read-active read-active)))

  (stop [component] (log/info "Stopping Twitterreader component")
    (reset! read-active false)
    (assoc component :read-active nil)))

(defn new-twitterreader [] (map->TwiterReader {}))
