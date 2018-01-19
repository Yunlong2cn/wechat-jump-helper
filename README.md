# wechat-jump-helper
一款JAVA版开源的微信跳一跳小程序辅助工具

## 使用
直接在 release 中下载最新版本，并确保您有 java 环境，adb 环境，通过命令执行即可
```
// java -jar wechat-jump-helper-[version].jar [path] [-auto-restart]
// 如下：
java -jar wechat-jump-helper-1.0.1.jar .
如需游戏结束后自动重启，执行如下命令(最后一个参数随意，你喜欢就好)：
java -jar wechat-jump-helper-1.0.1.jar . true
```

## 源码编译
克隆项目到本地，本项目采用 gradle 构建，请确保您有 gradle 环境，然后切换到项目目录，执行 gradle build 即可

## 难点

* 1、获取棋子中心点坐标
* 2、获取色块中心点坐标

## 获取坐标流程图
> 获取棋子坐标流程

![image](https://github.com/Yunlong2cn/wechat-jump-helper/blob/master/assets/%E5%BE%AE%E4%BF%A1%E8%B7%B3%E4%B8%80%E8%B7%B3%EF%BC%8C%E6%89%BE%E6%A3%8B%E5%AD%90%E4%B8%AD%E5%BF%83%E7%82%B9.png)
> 获取色块坐标流程

![image](https://github.com/Yunlong2cn/wechat-jump-helper/blob/master/assets/%E5%BE%AE%E4%BF%A1%E8%B7%B3%E4%B8%80%E8%B7%B3%EF%BC%8C%E6%89%BE%E8%89%B2%E5%9D%97%E4%B8%AD%E5%BF%83%E7%82%B9.png)

## 联系

QQ群：104725817 （请备注来源：Github）
