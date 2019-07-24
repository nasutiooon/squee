(ns squee.url)

(defn reset-token
  [host-uri username]
  (str host-uri "/api/token/" username "/reset"))

(defn anon-user
  [host-uri]
  (str host-uri "/api/user"))

(defn target-user
  [host-uri username]
  (str host-uri "/api/user/" username))

(defn forget-user
  [host-uri username]
  (str host-uri "/api/user/" username "/forget"))
