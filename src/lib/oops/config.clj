(ns oops.config
  (:require [oops.state :as state]
            [oops.debug :refer [log]]))

(def default-runtime-config
  {:object-access-validation true})

(def default-compiler-config
  {:diagnostics              true
   :object-access-validation :throw                                                                                           ; :warn false
   :runtime-config           default-runtime-config})

(def advanced-mode-compiler-config-overrides
  {:diagnostics false})

; -- helpers ----------------------------------------------------------------------------------------------------------------

(defn advanced-mode? []
  (if cljs.env/*compiler*
    (= (get-in @cljs.env/*compiler* [:options :optimizations]) :advanced)))

(defn prepare-default-config []
  (merge default-compiler-config (if (advanced-mode?) advanced-mode-compiler-config-overrides)))

(defn read-project-config []
  (if cljs.env/*compiler*
    (get-in @cljs.env/*compiler* [:options :external-config :oops/config])))                                                  ; https://github.com/bhauman/lein-figwheel/commit/80f7306bf5e6bd1330287a6f3cc259ff645d899b

(defn read-env-config []
  {})                                                                                                                         ; TODO: write a library for this

(defn ^:dynamic get-initial-compiler-config []
  {:post [(map? %)]}                                                                                                          ; TODO: validate config using spec or bhauman's tooling
  (merge (prepare-default-config) (read-project-config) (read-env-config)))

; -- public api--------------------------------------------------------------------------------------------------------------

(defn set-current-compiler-config! [new-config]
  {:pre [(map? new-config)]}
  (alter-var-root #'state/*compiler-config* (constantly new-config))
  new-config)

(defn get-current-compiler-config []
  (if-not (bound? #'state/*compiler-config*)
    (set-current-compiler-config! (get-initial-compiler-config))
    state/*compiler-config*))

(defn update-current-compiler-config! [f-or-map & args]
  (if (map? f-or-map)
    (update-current-compiler-config! merge f-or-map)
    (set-current-compiler-config! (apply f-or-map (get-current-compiler-config) args))))

; -- runtime macros ---------------------------------------------------------------------------------------------------------

(defmacro with-runtime-config [config & body]
  `(binding [oops.state/*runtime-config* (merge (get-current-runtime-config) ~config)]
     ~@body))

(defmacro gen-runtime-config []
  (:runtime-config (get-current-compiler-config)))

; -- icing ------------------------------------------------------------------------------------------------------------------

(defn diagnostics? []
  (true? (:diagnostics (get-current-compiler-config))))

(defn validate-object-access? []
  (let [config (get-current-compiler-config)]
    (and (:diagnostics config)
         (some? (:object-access-validation config)))))

(defn object-access-validation-mode []
  (let [config (get-current-compiler-config)]
    (if (:diagnostics config)
      (:object-access-validation config))))
