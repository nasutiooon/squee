(ns squee.user
  (:require
   [squee.request :as req]
   [squee.url :as url]))

(defrecord UserClient [host-uri content-type])

(defn user-client
  [config]
  (map->UserClient config))

(defn register
  [{:keys [host-uri] :as user-client} registree]
  (let [url    (url/anon-user host-uri)
        option (-> user-client
                   (select-keys [:content-type])
                   (assoc :params {:user registree}))]
    (req/post url option)))

(defn login
  [{:keys [host-uri] :as user-client} [username :as credential]]
  (let [url    (url/target-user host-uri username)
        option (-> user-client
                   (select-keys [:content-type])
                   (assoc :basic-auth credential))]
    (req/post url option)))

(defn update-password
  [{:keys [host-uri] :as user-client} [username :as credential] updatee]
  (let [url    (url/target-user host-uri username)
        option (-> user-client
                   (select-keys [:content-type])
                   (assoc :params {:user updatee}))]
    (req/put url option)))

(defn reset-password
  [{:keys [host-uri] :as user-client} {:keys [username reset-token]} resetee]
  (let [url    (url/forget-user host-uri username)
        option (-> user-client
                   (select-keys [:content-type])
                   (assoc :params {:user resetee}))]
    (req/put url option)))

(defn unregister
  [{:keys [host-uri] :as user-client} [username :as credential]]
  (let [url    (url/target-user host-uri username)
        option (-> user-client
                   (select-keys [:content-type])
                   (assoc :basic-auth  credential))]
    (req/delete url option)))
