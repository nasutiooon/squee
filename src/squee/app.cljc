(ns squee.app
  (:require
   [gilmour.jwt-encoder :as g.jwt]
   [squee.request :as req]
   [squee.url :as url]))

(defrecord AppClient [host-uri content-type])

(defn app-client
  [config]
  (map->AppClient config))

(defn- get-attached-jwt-encoder
  [component]
  (->> (vals component)
       (filter (partial satisfies? g.jwt/JwtEncoder))
       (first)))

(defn request-reset-token
  [{:keys [host-uri] :as app-client} username]
  (let [token  (g.jwt/encode (get-attached-jwt-encoder app-client)
                             {:username username})
        url    (url/reset-token host-uri username)
        option (-> app-client
                   (select-keys [:content-type])
                   (assoc :app-auth token))]
    (req/post url option)))

(defn read-auth-token
  [app-client token]
  (g.jwt/decode (get-attached-jwt-encoder app-client) token))
