package net.top_t;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.render.ps.PSTranscoder;
import org.apache.fop.svg.AbstractFOPTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.cyrillic.CyrillicRegistration;
import org.scilab.forge.jlatexmath.greek.GreekRegistration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * LaTextConvert used to convert LaTex code
 *
 */
public class LaTexConvert
{
    private static final int PDF = 0;
    private static final int PS = 1;
    private static final int EPS = 2;

    /**
     * convert LaTex code to .svg code
     * @param latex LaTex code
     * @param fontAsShapes  use shape
     * @param size the default TeXFont's point size
     * @param bkgTransparent if set background color to be transparent
     * @return
     * @throws IOException
     */
    public static String latexToSvgCode(String latex, boolean fontAsShapes, float size, boolean bkgTransparent) throws IOException {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);

        SVGGraphics2D g2 = new SVGGraphics2D(ctx, fontAsShapes);

        DefaultTeXFont.registerAlphabet(new CyrillicRegistration());
        DefaultTeXFont.registerAlphabet(new GreekRegistration());

        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);
        icon.setInsets(new Insets(5, 5, 5, 5));
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        g2.setSVGCanvasSize(new Dimension(width, height));
        Color backgroundColor = new Color(255, 255, 255);   //white
        if (bkgTransparent) {
            backgroundColor = new Color(255, 255, 255, 0); //transparent
        }
        g2.setColor(backgroundColor);

        g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());

        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        icon.paintIcon(jl, g2, 0, 0);

        boolean useCSS = true;
        StringWriter out = new StringWriter();

        g2.stream(out, useCSS);

        String svgStr = out.getBuffer().toString();
        out.flush();
        out.close();

        return svgStr;
    }

    /**
     * convert LaTex code to .svg file
     * @param latexCode LaTex formula
     * @param fontAsShapes  font as shapes
     * @param size the default TeXFont's point size
     * @param bkgTransparent set background to be transparent
     * @param svgFile
     */
    public static void toSvgFile(String latexCode, boolean fontAsShapes, float size, boolean bkgTransparent, File svgFile) {
        try {
            String svgCode = latexToSvgCode(latexCode, fontAsShapes, size, bkgTransparent);

            if (null != svgFile) {
                OutputStream outputStream = new FileOutputStream(svgFile);
                outputStream.write(svgCode.getBytes());
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * convert LaTex code to .png file
     * @param latexString LaTex formula
     * @param size the default TeXFont's point size
     * @param pngFile File object, where the png file will be stored to
     * @param isTransparent set the background color to be transparent
     * @throws IOException
     */
    public static void toPngFile(String latexString, float size, File pngFile, boolean isTransparent) throws IOException {
        TeXFormula formula = new TeXFormula(latexString);

        TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(size).build();
        icon.setInsets(new Insets(5, 5, 5, 5));

        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = image.createGraphics();
        if (isTransparent) {
            g2.setColor(new Color(255, 255, 255, 0));
        } else {
            g2.setColor(Color.white);
        }
        g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());

        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        icon.paintIcon(jl, g2, 0, 0);

        ImageIO.write(image, "png", pngFile.getAbsoluteFile());
    }

    /**
     * convert LaTex code to a .pdf file
     * @param latexCode LaTex code
     * @param fontAsShapes it can be set to true/false
     * @param size the default TeXFont's point size
     * @param bkgTransparent background color is transparent or not
     * @param pdfFile File object of PDF
     * @throws TranscoderException
     * @throws IOException
     */
    public static void toPdf(String latexCode, boolean fontAsShapes, float size, boolean bkgTransparent, File pdfFile) throws TranscoderException, IOException {
        svgTo(latexCode, fontAsShapes, size, bkgTransparent, PDF, pdfFile);
    }

    /**
     * convert LaTex code to ps file
     * @param latexCode LaTex code
     * @param fontAsShapes it can be set to true/false
     * @param size the default TeXFont's point size
     * @param bkgTransparent background color is transparent or not
     * @param psFile File object of ps
     * @throws IOException
     * @throws TranscoderException
     */
    public static void toPs(String latexCode, boolean fontAsShapes, float size, boolean bkgTransparent, File psFile) throws IOException, TranscoderException {
        svgTo(latexCode, fontAsShapes, size, bkgTransparent, PS, psFile);
    }

    /**
     * convert LaTex code to eps file
     * @param latexCode LaTex code
     * @param fontAsShapes it can be set to true/false
     * @param size the default TeXFont's point size
     * @param bkgTransparent background color is transparent or not
     * @param epsFile file object of eps
     * @throws IOException
     * @throws TranscoderException
     */
    public static void toEps(String latexCode, boolean fontAsShapes, float size, boolean bkgTransparent, File epsFile) throws IOException, TranscoderException {
        svgTo(latexCode, fontAsShapes, size, bkgTransparent, EPS, epsFile);
    }

    /**
     * convert LaTex code to given file type. support PDF/PS/EPS file
     * @param latexCode LaTex code
     * @param fontAsShapes it can be set to true/false
     * @param size the default TeXFont's point size
     * @param bkgTransparent background color is transparent or not
     * @param type file type of PDF, PS, EPS
     * @param output file of output
     * @throws IOException
     * @throws TranscoderException
     */
    private static void svgTo(String latexCode, boolean fontAsShapes, float size, boolean bkgTransparent, int type, File output) throws IOException, TranscoderException {
        AbstractFOPTranscoder trans;
        switch (type) {
            case PDF:
                trans = new PDFTranscoder();
                break;
            case PS:
                trans = new PSTranscoder();
                break;
            case EPS:
                trans = new EPSTranscoder();
                break;
            default:
                trans = null;
        }

        String svgCode = latexToSvgCode(latexCode, fontAsShapes, size, bkgTransparent);
        Reader reader = new StringReader(svgCode);
        TranscoderInput transcoderInput = new TranscoderInput(reader);
        OutputStream outputStream = new FileOutputStream(output);
        TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
        assert trans != null;
        trans.transcode(transcoderInput, transcoderOutput);
        outputStream.flush();
        outputStream.close();
    }

    public static void main( String[] args )
    {
        float SIZE = 50;

        String latex = "\\begin{array}{l}";
        latex += "\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\";
        latex += "\\det\\begin{bmatrix}a_{11}&a_{12}&\\cdots&a_{1n}\\\\a_{21}&\\ddots&&\\vdots\\\\\\vdots&&\\ddots&\\vdots\\\\a_{n1}&\\cdots&\\cdots&a_{nn}\\end{bmatrix}\\overset{\\mathrm{def}}{=}\\sum_{\\sigma\\in\\mathfrak{S}_n}\\varepsilon(\\sigma)\\prod_{k=1}^n a_{k\\sigma(k)}\\\\";
        latex += "\\sideset{_\\alpha^\\beta}{_\\gamma^\\delta}{\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}}\\\\";
        latex += "\\int_0^\\infty{x^{2n} e^{-a x^2}\\,dx} = \\frac{2n-1}{2a} \\int_0^\\infty{x^{2(n-1)} e^{-a x^2}\\,dx} = \\frac{(2n-1)!!}{2^{n+1}} \\sqrt{\\frac{\\pi}{a^{2n+1}}}\\\\";
        latex += "\\int_a^b{f(x)\\,dx} = (b - a) \\sum\\limits_{n = 1}^\\infty  {\\sum\\limits_{m = 1}^{2^n  - 1} {\\left( { - 1} \\right)^{m + 1} } } 2^{ - n} f(a + m\\left( {b - a} \\right)2^{-n} )\\\\";
        latex += "\\int_{-\\pi}^{\\pi} \\sin(\\alpha x) \\sin^n(\\beta x) dx = \\textstyle{\\left \\{ \\begin{array}{cc} (-1)^{(n+1)/2} (-1)^m \\frac{2 \\pi}{2^n} \\binom{n}{m} & n \\mbox{ odd},\\ \\alpha = \\beta (2m-n) \\\\ 0 & \\mbox{otherwise} \\\\ \\end{array} \\right .}\\\\";
        latex += "L = \\int_a^b \\sqrt{ \\left|\\sum_{i,j=1}^ng_{ij}(\\gamma(t))\\left(\\frac{d}{dt}x^i\\circ\\gamma(t)\\right)\\left(\\frac{d}{dt}x^j\\circ\\gamma(t)\\right)\\right|}\\,dt\\\\";
        latex += "\\begin{array}{rl} s &= \\int_a^b\\left\\|\\frac{d}{dt}\\vec{r}\\,(u(t),v(t))\\right\\|\\,dt \\\\ &= \\int_a^b \\sqrt{u'(t)^2\\,\\vec{r}_u\\cdot\\vec{r}_u + 2u'(t)v'(t)\\, \\vec{r}_u\\cdot\\vec{r}_v+ v'(t)^2\\,\\vec{r}_v\\cdot\\vec{r}_v}\\,\\,\\, dt. \\end{array}\\\\";
        latex += "\\end{array}";

        File imgFile = new File("/tmp/example1.png");
        File svgFile = new File("/tmp/example1.svg");
        File psFile = new File("/tmp/example1.ps");
        File epsFile = new File("/tmp/example1.eps");
        try {
            // generate png file with transparent background
            LaTexConvert.toPngFile(latex, SIZE, imgFile, true);
            // generate svg file with transparent background
            LaTexConvert.toSvgFile(latex, true, SIZE, true, svgFile);
            LaTexConvert.toPs(latex, true, SIZE, true, psFile);
            LaTexConvert.toEps(latex, true, SIZE, true, epsFile);
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }

        System.out.println( "Hello World!" );
    }
}
