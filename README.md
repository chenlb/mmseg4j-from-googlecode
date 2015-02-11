# mmseg4j-from-googlecode

主要是 mmseg4j-1.8.x 版本转为 maven 发布。在 solr/lucene 3.1.0 编译。

 * solr/lucene [3.2, 3.6]，[4.0, 4.2] 请使用 [mmseg4j-for-solr](https://github.com/chenlb/mmseg4j-for-solr) 的对应版本。
 * solr/lucene [4.3, ~] 请使用 [mmseg4j-solr](https://github.com/chenlb/mmseg4j-solr) 的对应版本。

另外 [mmseg4j-core](https://github.com/chenlb/mmseg4j-core) 也独立出一个项目。

不依赖 solr/lucene。目前的 mmseg4j-core-1.10.0 与 mmseg4j-1.8.5 功能基本一致。

## 1、mmseg4j 算法

mmseg4j 用 Chih-Hao Tsai 的 [MMSeg 算法](http://technology.chtsai.org/mmseg/)实现的中文分词器，并实现 lucene 的 analyzer 和 solr 的TokenizerFactory 以方便在Lucene和Solr中使用。

## 2、MMSeg 算法有两种分词方法：Simple和Complex

都是基于正向最大匹配。Complex 加了四个规则过虑。官方说：词语的正确识别率达到了 98.41%。mmseg4j 已经实现了这两种分词算法。

 * 1.5版的分词速度simple算法是 1100kb/s左右、complex算法是 700kb/s左右，（测试机：AMD athlon 64 2800+ 1G内存 xp）。
 * 1.6版在complex基础上实现了最多分词(max-word)。“很好听” -> "很好|好听"; “中华人民共和国” -> "中华|华人|共和|国"; “中国人民银行” -> "中国|人民|银行"。
 * 1.7-beta 版, 目前 complex 1200kb/s左右, simple 1900kb/s左右, 但内存开销了50M左右. 上几个版都是在10M左右.
 * 1.8 后,增加 CutLetterDigitFilter 过虑器，切分“字母和数”混在一起的过虑器。比如：mb991ch 切为 "mb 991 ch"。

[mmseg4j实现的功能详情](https://raw.githubusercontent.com/chenlb/mmseg4j-from-googlecode/branches/mmseg4j-1.8/CHANGES.txt)

## 3、example

在 com.chenlb.mmseg4j.example包里的类示例了三种分词效果。

## 4、analysis

在 com.chenlb.mmseg4j.analysis包里扩展lucene analyzer。MMSegAnalyzer默认使用max-word方式分词。

## 5、在 solr 中使用

在 com.chenlb.mmseg4j.solr包里扩展solr tokenizerFactory。
在 solr的 schema.xml 中定义 field type如：

```xml
<fieldType name="textComplex" class="solr.TextField" >
  <analyzer>
    <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="complex" dicPath="dic"/>
    <filter class="com.chenlb.mmseg4j.solr.CutLetterDigitFilterFactory" />
  </analyzer>
</fieldType>
<fieldType name="textMaxWord" class="solr.TextField" >
  <analyzer>
    <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="max-word" dicPath="dic"/>
  </analyzer>
</fieldType>
<fieldType name="textSimple" class="solr.TextField" >
  <analyzer>
    <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="simple" dicPath="n:/OpenSource/apache-solr-1.3.0/example/solr/my_dic"/>
  </analyzer>
</fieldType>
```

一般都需要加 com.chenlb.mmseg4j.solr.CutLetterDigitFilterFactory 来分开字母与数字连接的。

dicPath 指定词库位置（每个MMSegTokenizerFactory可以指定不同的目录，当是相对目录时，是相对 solr.home 的目录），mode 指定分词模式（simple|complex|max-word，默认是max-word）。

## 6、运行示例

词典用mmseg.dic.path属性指定、在classpath 目录下或在当前目录下的data目录，默认是 classpath/data 目录。如果使用 mmseg4j-*-with-dic.jar 包可以不指定词库目录（如果指定也可以，它们也可以被加载）。

```bash
java -jar mmseg4j-core-1.8-with-dic.jar '这里是字符串'

java -cp .;mmseg4j-1.6.jar -Dmmseg.dic.path=./other-dic com.chenlb.mmseg4j.example.Simple '这里是字符串'

java -cp .;mmseg4j-1.6.jar com.chenlb.mmseg4j.example.MaxWord '这里是字符串'
```

## 7、一些字符的处理

英文、俄文、希腊、数字（包括①㈠⒈）的分出一连串的。目前版本没有处理小数字问题，
如ⅠⅡⅢ是单字分，字库(chars.dic)中没找到也单字分。

## 8、词库：

 * data/chars.dic 是单字与语料中的频率，一般不用改动，1.5版本中已经加到mmseg4j的jar里了，我们不需要关心它，当然你在词库目录放这个文件可能覆盖它。
 * data/units.dic 是单字的单位，默认读jar包里的，你也可以自定义覆盖它。
 * data/words.dic 是词库文件，一行一词，当然你也可以使用自己的，1.5版本使用 sogou 词库，1.0的版本是用 rmmseg 带的词库。
 * data/wordsxxx.dic 1.6版支持多个词库文件，data 目录（或你定义的目录）下读到"words"前缀且".dic"为后缀的文件。如：data/words-my.dic。

## 9、MMseg4jHandler:

添加 MMseg4jHandler 类，可以在solr中用url的方式来控制加载检测词库。(后续不维护此功能)。参数：

 * dicPath 是指定词库的目录，特性与MMSegTokenizerFactory中的dicPath一样（相对目录是，是相对 solr.home）。
 * check 是指是否检测词库，其值是true 或 on。
 * reload 是否尝试加载词库，其值是 true 或 on。此值为 true，会忽视 check 参数。
 
solrconfig.xml：

```xml
<requestHandler name="/mmseg4j" class="com.chenlb.mmseg4j.solr.MMseg4jHandler" >
	<lst name="defaults">
		<str name="dicPath">dic</str>
	</lst>
</requestHandler>
```

此功能可以让外置程序做相关的控制，如：尝试加载词库，然后外置程序决定是否重做索引。

## 10、只结合 lucene 使用

pom.xml

```xml
<dependency>
	<groupId>com.chenlb.mmseg4j</groupId>
	<artifactId>mmseg4j-dic</artifactId>
	<version>1.8.6</version>
</dependency>
<dependency>
	<groupId>com.chenlb.mmseg4j</groupId>
	<artifactId>mmseg4j-analysis</artifactId>
	<version>1.8.6</version>
</dependency>
```

ComplexAnalyzerDemo

```java
Analyzer analyzer = new ComplexAnalyzer();
String txt = "研究生命起源";

TokenStream ts = analyzer.tokenStream("txt", new StringReader(txt));

for(Token t= new Token(); (t= TokenUtils.nextToken(ts, t)) !=null;) {
	System.out.println(t);
}

ts.close();
analyzer.close();
```

## 11、反馈

可以在新版 [mmseg4j-solr issues](https://github.com/chenlb/mmseg4j-solr/issues) 提出希望 mmseg4j 有的功能或 bug。

[官方博客](http://blog.chenlb.com/category/mmseg4j) 有一些关于 solr 使用 mmseg4j 旧的文章。
