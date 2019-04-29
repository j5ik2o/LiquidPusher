# LiquidPusherの使い方
## 概要
LiquidTapのPython用のライブラリを読んで、同じように作用するものをScalaで作りました。（JavaScriptとPythonしかないって不親切じゃね？）
※LiquidTapはPusher（WebSocket接続を楽にしてくれる）をLiquid用にラップしたものです。

５月からのLiquid　Bot大会に向けての一時しのぎなので、再接続などは全くやってないです。。。
Bot作り終わって時間できたら大幅改造します＾＾

質問があったらTwitterまでどうぞ@take_btc

## 依存
```sbt:build.sbt
libraryDependencies = "com.github.BambooTuna" %% "liquidpusher" % "0.1.0-SNAPSHOT"
```

## サンプル
詳細は`Sample`内の`Main.scala`を参照
APIKEYがないのでPrivateChannelは購読できません。
```sbtshell
$ sbt Sample/run
```

## Privateチャンネル購読
`PusherAuthorizer("key", "secret")`に自分のAPIKEYをセットしてください  
また、PrivateChannelは`pusher#subscribePrivate`を使ってください。