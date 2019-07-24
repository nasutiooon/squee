(ns squee.request
  (:require
   #?(:clj  [clj-http.client :as client]
      :cljs [cljs-http.client :as client])))

(defn- wrap-basic-auth
  [{:keys [basic-auth] :as option}]
  #?(:clj option
     :cljs (if basic-auth
             (assoc option :basic-auth (zipmap [:user :password] basic-auth))
             option)))

(defn- wrap-token-auth
  [option scheme token]
  (let [header (str scheme " " token)]
    (assoc-in option [:headers "authorization"] header)))

(defn- wrap-reset-token-auth
  [{:keys [reset-auth] :as option}]
  (if reset-auth
    (-> option
        (dissoc :reset-auth)
        (wrap-token-auth "ImpalaReset" reset-auth))
    option))

(defn- wrap-app-token-auth
  [{:keys [app-auth] :as option}]
  (if app-auth
    (-> option
        (dissoc :app-auth)
        (wrap-token-auth "ImpalaApp" app-auth))
    option))

(defn- wrap-auth
  [option]
  (-> option wrap-basic-auth wrap-reset-token-auth wrap-app-token-auth))

(defn- wrap-params
  [{:keys [params content-type] :as option}]
  #?(:clj  (let [content-type (or content-type :json)
                 option       (-> option
                                  (dissoc :content-type)
                                  (assoc :accept content-type
                                         :as content-type))]
             (if params
               (-> option
                   (dissoc :params)
                   (assoc :form-params params :content-type content-type))
               option))
     :cljs (let [k (case content-type
                     :transit+json    :transit-params
                     :transit+msgpack :transit-params
                     :edn             :edn-params
                     :json-params)]
             (-> option
                 (dissoc :params :content-type)
                 (assoc k params)))))

(defn- request
  [option]
  (-> option wrap-auth wrap-params))

(defn post
  [url option]
  (client/post url (request option)))

(defn put
  [url option]
  (client/put url (request option)))

(defn delete
  [url option]
  (client/delete url (request option)))
