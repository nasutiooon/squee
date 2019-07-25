(ns squee.app
  (:require
   [gilmour.jwt-encoder :as g.jwt]
   [squee.request :as req]
   [squee.url :as url]))

(defrecord AppClient [host-uri content-type guardian passport])

(defn app-client
  [config]
  (map->AppClient config))

(defn request-reset-token
  [{:keys [host-uri guardian] :as app-client} username]
  (let [token  (g.jwt/encode guardian {:username username})
        url    (url/reset-token host-uri username)
        option (-> app-client
                   (select-keys [:content-type])
                   (assoc :app-auth token))]
    (req/post url option)))

(defn read-auth-token
  [{:keys [passport]} token]
  (g.jwt/decode passport token))
