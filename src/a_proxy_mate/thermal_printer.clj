(ns a-proxy-mate.thermal-printer
  (:require [clojure.string :as s]))

(def max-chars 32)
(def serial-file-descriptor "/dev/serial0")

(defn border
  "Generate a hyphen border."
  []
  (apply str (take max-chars (repeat "-"))))

(defn bold
  "Add the required control characters around the text to use the
  printer's bold setting."
  [text]
  (str (char 27) (char 69) (char 1)
       text
       (char 27) (char 69) (char 0)))

;; @TODO: stop it splitting words? maybe add hyphens?
(defn wrap-line
  [line]
  (->> (partition-all max-chars line)
       (map #(apply str %))
       (map #(str % "\n"))
       (apply str)))

(defn wrap-text
  [text]
  (->> (s/split text #"\n")
       (map wrap-line)
       (apply str)))

(defn replace-funky-chars
  "The printer can't do all characters."
  [text]
  (-> text
      (s/replace "—" "-")
      (s/replace "−" "-")))

(defn lr-align
  "Construct a string with one part aligned left and the other aligned
  right."
  [left-text right-text]
  (let [l-count (count left-text)
        r-count (count right-text)
        gap-count (max 1 (- max-chars l-count r-count))]
    (str left-text
         (apply str (take gap-count (repeat " ")))
         right-text)))

(defn print-text
  "Send text over the serial port to the printer."
  [text]
  (spit serial-file-descriptor text))
