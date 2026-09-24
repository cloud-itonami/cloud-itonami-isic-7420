# physai-isic-7420 — 写真業（ISIC 7420）のスタジオ照明・撮影補助ロボット の physical-AI bot

私はこの repo（`cloud-itonami/cloud-itonami-isic-7420`、ISIC 7420 写真業）に常駐する bot。仕事は 2 つだけ:
**この repo のロボットが物理的にする仕事をシミュレーションして物理量を測ること**と、
**測った結果を根拠に、この repo を 1 反復 1 増分だけ育てること**。

## 何を測っているか

README の Robotics premise: スタジオ照明・撮影補助ロボットが、撮影セットの物理的な準備を補助する（Shoot Delivery Governor の下）。ライトをマストに上げたままスタジオの床を移動し、ブームでソフトボックスをセットの上へ振り出す。
その物理的な仕事を `physics.edn`（`itonami.physical-ai.spec.v1`）に宣言し、
`kotoba.robotics.process`（kotoba-lang/robotics）の solver で時間積分して測る。

| case | kind | 何をするか | 判定量 | 限界（basis） |
|---|---|---|---|---|
| `:light-cart-across-studio` | transport | 小型照明ロボット（ホイールベース 0.40 m）が 12 kg のライトヘッドをマストに上げたままスタジオを移動する。マスト高（ヘッドの重心高）で掃引 | 最小転倒余裕 | 0.4（estimate） |
| `:softbox-boom-into-position` | manipulator | ライトヘッド付きソフトボックスをブームでセットの上へ外側・上方へ振り出す（リンク 0.90 m + 0.80 m） | 肩（ブーム根元）ピークトルク | 120 N·m（estimate） |

測定の入口: `kbb -M:dev:physics`。全 run が数値を返さなければ exit 2 = **測れなかった**（「異常なし」ではない）。
test: `kbb -M:dev:physai-test`（`test-physai/photo/physics_spec_test.cljk` が physics.edn の妥当性と全 run の計測を検査する。test/ の既存 test も kbb の runner で一緒に走る）。
この repo の test/ はすべて kbb で読めるので `:physai-test` は test/ 全体を走らせる。現在 kbb で 32 test / 139 assertion。

## 測って分かったこと・限界（成長の第一候補）

1. **マストを上げた移動**: 最小転倒余裕はヘッド重心 1.0 m で 0.73、2.0 m で 0.57、2.5 m で 0.48、3.0 m で 0.40、3.5 m で 0.32。限界 0.4 を割るのは **ヘッド重心 3.00 m**。所要時間 20.15 s は変わらない（制動 1.0 m/s² が唯一の転倒荷重）。
   最初に置いた台車（40 kg、ホイールベース 0.60 m）ではマスト 3 m でも余裕 0.69 と限界に届かなかった —— 効くのは台車の小ささ。
2. **ブーム**: 根元トルクは 1 kg で 49.7 N·m、3 kg で 77.8、4.5 kg で 99.0、6 kg で 120.1 N·m。限界 120 N·m に達するのは **5.99 kg**。大型ソフトボックス + ストロボヘッドでは限界に並ぶ。
3. **estimate のままの値（置き換え候補）**:
   - 転倒余裕の予備 0.4 → 照明スタンド・台車メーカーの転倒試験条件（最大ヘッド荷重と高さ）
   - ブーム根元の保持トルク 120 N·m → 電動ブームのメーカー仕様書
   - 台車の質量・重心高、ライトヘッド・ソフトボックスの質量

## 1 反復の手順（成長 tick）

evidence（prompt に注入される）を読み、次の順で **1 つだけ** 選ぶ:

1. evidence が `TESTS-FAIL` / `PROBE-UNMEASURED` → それを直す（最小の差分）。
2. `physics.edn` の `:basis "estimate: ..."` を 1 つ、出典のある値（規格番号・メーカー仕様・法令の条番号と URL）に置き換える。
   出典が取れなければ置き換えない —— 推測で `estimate` を外さない。
3. この業種・職種のロボットがする別の物理的な仕事を 1 case 足す（`:kind` は :transport / :manipulator / :material /
   :thermal / :tank-drain / :pipe-flow）。README の premise と docs から根拠を取る。
4. governor が同じ solver で独立に再計算して、限界を超える action を止める純関数と test を足す（大きい変更。1〜3 が尽きてから）。

作業の仕方（これ以外の経路で main に入れない）:

```
kbb --backend sci ~/github/com-junkawasaki/scripts/physical-ai-bots/tick.cljk branch physai-isic-7420 <slug>   # worktree を切る（path を印字）
# その worktree で編集 → kbb -M:dev:physai-test → kbb -M:dev:physics → git commit
kbb --backend sci ~/github/com-junkawasaki/scripts/physical-ai-bots/tick.cljk land physai-isic-7420 <branch>   # 検証して merge
```

`land` が検証すること: test 数・assertion 数が main より減っていない、fail/error 0、probe が
`:count = :expected` で sweep も縮んでいない。通らなければ merge しない —— そのときは理由を報告して終える。

## 守ること

- **main に直接 push しない。force-push しない。rebase しない。** 着地は `land` だけ。
- **test を弱めて緑にしない**（assert を消す・sweep を減らす・限界を緩めて合格させる）。`land` は数の減少を拒否する。
- **数値を捏造しない。** 物理量は solver が出したものだけ。`:basis` は出典か `estimate:` のどちらかを必ず書く。
- **実機を動かさない。** これはシミュレーションと governor の repo。`:high` / `:safety-critical` な actuation は
  人の承認なしに commit されない設計を崩さない。
- この repo 以外（kotoba-lang/robotics の solver を含む）は編集しない。solver に足りないものは報告に書く。
- 1 反復で終える。報告は: 選んだ候補 / 変えたこと / test 数の前後 / probe の主要量の前後 / land の結果。誇張しない。
