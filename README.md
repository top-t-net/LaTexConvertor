# LaTexConvertor
Convert LaTex code to .svg, .png, .pdf, .ps, .eps file. It's based on https://github.com/opencollab/jlatexmath.

# Getting started
Including the Java library in your project
```xml
    <dependency>
      <groupId>net.top-t</groupId>
      <artifactId>LaTexConvertor</artifactId>
      <version>0.3.0</version>
    </dependency>
```
# Usage
```java
float SIZE = 50;

String latex = "xxxxx";

File imgFile = new File("/tmp/example1.png");
File svgFile = new File("/tmp/example1.svg");
File psFile = new File("/tmp/example1.ps");
File epsFile = new File("/tmp/example1.eps");

// generate png file with transparent background
LaTexConvert.toPngFile(latex, SIZE, imgFile, true);
// generate svg file with transparent background
LaTexConvert.toSvgFile(latex, true, SIZE, true, svgFile);
LaTexConvert.toPs(latex, true, SIZE, true, psFile);
LaTexConvert.toEps(latex, true, SIZE, true, epsFile);


```

# 中文
## LaTex转换器
将LaTex的编码转换为.svg, .png, .pdf, .ps, .eps文件

# License
GNU GPL v2.0
