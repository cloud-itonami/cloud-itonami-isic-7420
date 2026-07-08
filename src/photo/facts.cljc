(ns photo.facts
  "Per-jurisdiction photographic/image-rights regulatory catalog -- the
  G2-style spec-basis table the Shoot Delivery Governor checks every
  `:shootplan/verify` proposal against ('did the advisor cite an
  OFFICIAL public source for this jurisdiction's image-rights/
  personal-data-in-images framework, or did it invent one?').

  Coverage is reported HONESTLY (see `coverage`), the same discipline
  every sibling actor's `facts` namespace uses: a jurisdiction not in
  this table has NO spec-basis, full stop -- the advisor must not
  fabricate one, and the governor holds if it tries.

  Seed values are drawn from each jurisdiction's official image-
  rights/data-protection authority (see `:provenance`); they are a
  STARTING catalog, not a from-scratch survey of all ~194
  jurisdictions. Extending coverage is additive: add one map to
  `catalog`, cite a real source, done -- never invent a jurisdiction's
  requirements to make coverage look bigger.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the client-
  consent/shoot-plan/model-release-verification/image-delivery
  evidence set this blueprint's own Offer names; `:legal-basis` /
  `:owner-authority` / `:provenance` are the G2 citation the governor
  requires before any `:actuation/deliver-image-set` proposal can
  commit."
  {"JPN" {:name "Japan"
          :owner-authority "個人情報保護委員会 (Personal Information Protection Commission)"
          :legal-basis "個人情報の保護に関する法律 (Act on the Protection of Personal Information) / 肖像権・パブリシティ権に関する判例法理 (最高裁判所昭和44年12月24日大法廷判決等)"
          :national-spec "肖像を含む個人情報の取扱いおよび被写体の同意取得基準"
          :provenance "https://www.ppc.go.jp/personalinfo/legal/"
          :required-evidence ["顧客同意記録 (client-consent-record)"
                              "撮影計画記録 (shoot-plan-record)"
                              "肖像使用許諾確認記録 (model-release-verification-record)"
                              "画像納品記録 (image-delivery-record)"]}
   "USA" {:name "United States"
          :owner-authority "Federal Trade Commission (FTC) / state right-of-publicity statutes"
          :legal-basis "Restatement (Second) of Torts §652C (Appropriation of Name or Likeness) / e.g. Cal. Civ. Code §3344"
          :national-spec "Right-of-publicity and model-release requirements for commercial use of a person's likeness"
          :provenance "https://www.ftc.gov/business-guidance/privacy-security"
          :required-evidence ["Client consent record"
                              "Shoot-plan record"
                              "Model-release verification record"
                              "Image-delivery record"]}
   "GBR" {:name "United Kingdom"
          :owner-authority "Information Commissioner's Office (ICO)"
          :legal-basis "UK GDPR Article 6/9 / Data Protection Act 2018"
          :national-spec "Lawful-basis and special-category-data (images of identifiable individuals) processing requirements"
          :provenance "https://ico.org.uk/for-organisations/uk-gdpr-guidance-and-resources/"
          :required-evidence ["Client consent record"
                              "Shoot-plan record"
                              "Model-release verification record"
                              "Image-delivery record"]}
   "DEU" {:name "Germany"
          :owner-authority "Der Bundesbeauftragte für den Datenschutz und die Informationsfreiheit (BfDI)"
          :legal-basis "Kunsturhebergesetz (KUG) §22-23 (Recht am eigenen Bild)"
          :national-spec "Einwilligungserfordernisse für die Verbreitung und öffentliche Zurschaustellung von Bildnissen"
          :provenance "https://www.bfdi.bund.de/"
          :required-evidence ["Einwilligungsprotokoll (client-consent-record)"
                              "Aufnahmeplanprotokoll (shoot-plan-record)"
                              "Bildnisfreigabenachweis (model-release-verification-record)"
                              "Bildlieferungsprotokoll (image-delivery-record)"]}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO spec-basis,
  and the governor must hold any proposal that tries to deliver an
  image set on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions actually
  have a spec-basis entry. Never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-isic-7420 R0: " (count catalog)
                 " jurisdictions seeded with an official spec-basis. "
                 "This is a starting catalog, not a survey of all ~194 "
                 "jurisdictions -- extend `photo.facts/catalog`, "
                 "never fabricate a jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings) satisfy
  every evidence item listed for `iso3`? Missing spec-basis -> never
  satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))
