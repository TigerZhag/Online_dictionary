# How to use

## 初始化

```java
/**
  * 初始化,拷贝辞典数据库文件
  * @param contex
  */
static void Translator.init(Context con);
```

请在Application中调用Translator.init()静态方法,该方法在第一次启动应用时将拷贝辞典文件到数据库目录下

## 设置回调

```java
/**
 * @param listener 检索结果回调
 */
translator.setListener(OnSearchListener listener);

interface OnSearchListener {
    void onSearchResult(List<TransResult> results);
}
```

## 检索结果bean

```java
public class TransResult {
    public String word;             // 检索到的单词
    public String us_pronunciation; // 美国发音
    public String uk_pronunciation; // 英国发音
    public String meaning;          // 含义
    public String example;          // 例句
}
```

## 准确检索

```java  
/**  
 * @param query 需要检索的单词  
 */  
translator.search(String query);

```
## 模糊检索

```java
/**  
 * @param query 需要检索的字符串  
 * @param isFuzzy 是否需要模糊检索 true为模糊检索,false为精确检索
 */  
translator.search(String query, boolean isFuzzy);

/**
 * 检索结果分页
 * @param query 需要检索的单词
 * @param pageCount 每页最多记录数
 * @param pageNum 页码
 */
translator.search(String query, int pageCount ,int pageNum);
```

## 释放资源

```java
/**
 * 关闭数据库连接
 */
public void release()
```

