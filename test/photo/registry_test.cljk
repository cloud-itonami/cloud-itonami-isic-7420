(ns photo.registry-test
  (:require [clojure.test :refer [deftest is]]
            [photo.registry :as r]))

;; ----------------------------- model-release-coverage-insufficient? -----------------------------

(deftest not-insufficient-when-fully-covered
  (is (not (r/model-release-coverage-insufficient?
            {:subjects-requiring-release #{:subject-a}
             :subjects-with-signed-release #{:subject-a}})))
  (is (not (r/model-release-coverage-insufficient?
            {:subjects-requiring-release #{:subject-a}
             :subjects-with-signed-release #{:subject-a :subject-b}}))))

(deftest insufficient-when-not-fully-covered
  (is (r/model-release-coverage-insufficient?
       {:subjects-requiring-release #{:subject-a :subject-b}
        :subjects-with-signed-release #{:subject-a}})))

(deftest insufficient-is-true-on-missing-fields
  (is (r/model-release-coverage-insufficient? {:subjects-requiring-release #{:subject-a}}))
  (is (not (r/model-release-coverage-insufficient? {}))))

;; ----------------------------- register-image-set-delivery -----------------------------

(deftest delivery-is-a-draft-not-a-real-delivery
  (let [result (r/register-image-set-delivery "engagement-1" "JPN" 0)]
    (is (nil? (get-in result ["certificate" "proof"])))
    (is (= (get-in result ["certificate" "issued_by_registry"]) false))
    (is (= (get-in result ["certificate" "status"]) "draft-unsigned"))))

(deftest delivery-assigns-delivery-number
  (let [result (r/register-image-set-delivery "engagement-1" "JPN" 7)]
    (is (= (get result "delivery_number") "JPN-DEL-000007"))
    (is (= (get-in result ["record" "engagement_id"]) "engagement-1"))
    (is (= (get-in result ["record" "kind"]) "image-set-delivery-draft"))
    (is (= (get-in result ["record" "immutable"]) true))))

(deftest delivery-validation-rules
  (is (thrown? Exception (r/register-image-set-delivery "" "JPN" 0)))
  (is (thrown? Exception (r/register-image-set-delivery "engagement-1" "" 0)))
  (is (thrown? Exception (r/register-image-set-delivery "engagement-1" "JPN" -1))))

(deftest history-is-append-only
  (let [c1 (r/register-image-set-delivery "engagement-1" "JPN" 0)
        hist (r/append [] c1)
        c2 (r/register-image-set-delivery "engagement-2" "JPN" 1)
        hist2 (r/append hist c2)]
    (is (= 2 (count hist2)))
    (is (= "JPN-DEL-000000" (get-in hist2 [0 "record_id"])))
    (is (= "JPN-DEL-000001" (get-in hist2 [1 "record_id"])))))
