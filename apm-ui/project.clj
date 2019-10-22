(defproject a-proxy-mate "0.1.0-SNAPSHOT"
  :dependencies [[cljs-http "0.1.46"]
                 [hiccup-icons "0.4.1"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520" :exclusions [com.google.javascript/closure-compiler-unshaded
                                                                    org.clojure/google-closure-library]]
                 [reagent "0.8.1"]
                 [re-frame "0.10.9"]
                 [thheller/shadow-cljs "2.8.62"]]

  :plugins []

  :min-lein-version "2.5.3"

  :jvm-opts ["-Xmx700m"]

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :aliases {"dev"  ["with-profile" "dev" "run" "-m" "shadow.cljs.devtools.cli" "watch" "app"]
            "prod" ["with-profile" "prod" "run" "-m" "shadow.cljs.devtools.cli" "release" "app"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]]}
   :prod {}})
