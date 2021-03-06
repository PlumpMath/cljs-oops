// Clojure v1.9.0-alpha15, ClojureScript v1.9.521, js-beautify v1.6.12
// ----------------------------------------------------------------------------------------------------------
// COMPILER CONFIG:
//   arena/ocall_dynamic.cljs [core]
//   {:elide-asserts true,
//    :external-config
//    {:oops/config {:key-set :core, :key-get :core},
//     :devtools/config {:silence-optimizations-warning true}},
//    :main oops.arena.ocall-dynamic,
//    :optimizations :advanced,
//    :output-dir "test/resources/.compiled/ocall-dynamic-core/_workdir",
//    :output-to "test/resources/.compiled/ocall-dynamic-core/main.js",
//    :pseudo-names true}
// ----------------------------------------------------------------------------------------------------------

// SNIPPET #1:
//   (testing "simple dynamic ocall"
//     (ocall+ #js {"f" (fn [] 42)} (identity "f") "p1" "p2"))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

var call_info_1 = oops.core.get_selector_call_info_dynamically({
    f: function() {
      return 42
    }
  }, "f"),
  fn_1 = call_info_1[1];
null != fn_1 && fn_1.call(call_info_1[0], "p1", "p2");

// SNIPPET #2:
//   (testing "retageted dynamic ocall"
//     (ocall+ #js {"a" #js {"f" (fn [] 42)}} (identity "a.f") "p1" "p2"))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

var call_info_2 = oops.core.get_selector_call_info_dynamically({
    a: {
      f: function() {
        return 42
      }
    }
  }, "a.f"),
  fn_2 = call_info_2[1];
null != fn_2 && fn_2.call(call_info_2[0], "p1", "p2");
