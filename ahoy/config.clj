(ns ahoy.config)

(def port (Integer/parseInt
           (or (System/getenv "PORT") "6900")))

(def url (System/getenv "FURL"))

