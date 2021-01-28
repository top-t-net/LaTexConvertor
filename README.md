# LaTexConvertor
Convert LaTex code to .svg, .png file. It's based on https://github.com/opencollab/jlatexmath.

# Getting started
Including the Java library in your project
```xml
    <dependency>
      <groupId>net.top-t</groupId>
      <artifactId>LaTexConvertor</artifactId>
      <version>0.2.0</version>
    </dependency>
```
# Usage
```java
String latexStr = "xxx";
File pngFile = new File("/tmp/example1.png");
File svgFile = new File("/tmp/example2.svg");
// generate png file with transparent background
LaTexConvert.toPngFile(latexStr, pngFile, true);
// generate svg file with transparent background
LaTexConvert.toSvgFile(latexStr, true, true, svgFile);
```

# 中文
## LaTex转换器
将LaTex的编码转换为svg或者png文件

# License
GNU GPL v2.0
