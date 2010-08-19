/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.codegen;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.mysema.codegen.model.ClassType;
import com.mysema.codegen.model.Parameter;
import com.mysema.codegen.model.SimpleType;
import com.mysema.codegen.model.Type;
import com.mysema.codegen.model.Types;

public class JavaWriterTest {

    private static final Transformer<Parameter,Parameter> transformer = new Transformer<Parameter,Parameter>(){
        @Override
        public Parameter transform(Parameter input) {
            return input;
        }	
    };
    
    private StringWriter w;
    
    private CodeWriter writer;

    private Type testType, testType2, testSuperType, testInterface1, testInterface2;
    
    private static void match(String resource, String text) throws IOException{
        // TODO : try to compile ?
        String expected = IOUtils.toString(JavaWriterTest.class.getResourceAsStream(resource),"UTF-8").replace("\r\n", "\n").trim();
        String actual = text.trim();
        assertEquals(expected, actual);
    }

    @Before
    public void setUp(){
        w = new StringWriter();
        writer = new JavaWriter(w);
        testType = new ClassType(JavaWriterTest.class);
        testType2 = new SimpleType("com.mysema.codegen.Test","com.mysema.codegen","Test");
        testSuperType = new SimpleType("com.mysema.codegen.Superclass","com.mysema.codegen","Superclass");
        testInterface1 = new SimpleType("com.mysema.codegen.TestInterface1","com.mysema.codegen","TestInterface1");
        testInterface2 = new SimpleType("com.mysema.codegen.TestInterface2","com.mysema.codegen","TestInterface2");
    }

    
    @Test
    public void testBasic() throws IOException {        
        writer.packageDecl("com.mysema.codegen");
        writer.imports(IOException.class, StringWriter.class, Test.class);
        writer.beginClass(testType);
        writer.annotation(Test.class);
        writer.beginPublicMethod(Types.VOID, "test");
        writer.line("// TODO");
        writer.end();
        writer.end();
        
        match("/testBasic", w.toString());
    }
    
    @Test
    public void testExtends() throws IOException{
        writer.beginClass(testType2, testSuperType);
        writer.end();
        
        match("/testExtends", w.toString());
    }
    
    @Test
    public void testImplements() throws IOException{
        writer.beginClass(testType2, null, testInterface1,testInterface2);
        writer.end();
        
        match("/testImplements", w.toString());
    }
    
    @Test
    public void testInterface() throws IOException{
        writer.packageDecl("com.mysema.codegen");
        writer.imports(IOException.class, StringWriter.class, Test.class);
        writer.beginInterface(testType);
        writer.end();
        
        match("/testInterface", w.toString());
    }
    
    @Test
    public void testInterface2() throws IOException{
        writer.beginInterface(testType2, testInterface1);
        writer.end();
        
        match("/testInterface2", w.toString());
    }
    
    @Test
    public void testJavadoc() throws IOException{
        writer.packageDecl("com.mysema.codegen");
        writer.imports(IOException.class, StringWriter.class, Test.class);
        writer.javadoc("JavaWriterTest is a test class");
        writer.beginClass(testType);
        writer.end();
                
        match("/testJavadoc", w.toString());
    }

    
    @Test
    public void testAnnotations() throws IOException{
        writer.packageDecl("com.mysema.codegen");
        writer.imports(IOException.class, StringWriter.class);
        writer.annotation(Entity.class);
        writer.beginClass(testType);
        writer.annotation(Test.class);
        writer.beginPublicMethod(Types.VOID, "test");
        writer.end();
        writer.end();
                
        match("/testAnnotations", w.toString());
    }
    
    @Test
    public void testAnnotations2() throws IOException{
        writer.packageDecl("com.mysema.codegen");
        writer.imports(IOException.class.getPackage(), StringWriter.class.getPackage());
        writer.annotation(Entity.class);
        writer.beginClass(testType);
        writer.annotation(new Test(){
            @Override
            public Class<? extends Throwable> expected() {
                // TODO Auto-generated method stub
                return null;
            }
            @Override
            public long timeout() {

                return 0;
            }
            @Override
            public Class<? extends Annotation> annotationType() {
                return Test.class;
            }});
        writer.beginPublicMethod(Types.VOID, "test");
        writer.end();
        writer.end();
                
        match("/testAnnotations2", w.toString());
    }
    
    @Test
    public void testFields() throws IOException{
        writer.beginClass(testType);
        // private
        writer.privateField(Types.STRING, "privateField");
        writer.privateStaticFinal(Types.STRING, "privateStaticFinal", "\"val\"");
        // protected
        writer.protectedField(Types.STRING,"protectedField");
        // field
        writer.field(Types.STRING,"field");
        // public
        writer.publicField(Types.STRING,"publicField");
        writer.publicStaticFinal(Types.STRING, "publicStaticFinal", "\"val\"");
        writer.publicFinal(Types.STRING, "publicFinalField");
        writer.publicFinal(Types.STRING, "publicFinalField2", "\"val\"");
        
        writer.end();
        
        match("/testFields", w.toString());
    }
    
    @Test
    public void testMethods() throws IOException{
        writer.beginClass(testType);
        // private
        
        // protected
        
        // method
        
        // public
        writer.beginPublicMethod(Types.STRING, "publicMethod", Arrays.asList(new Parameter("a", Types.STRING)), transformer);
        writer.line("return null;");
        writer.end();
        
        writer.beginStaticMethod(Types.STRING, "staticMethod", Arrays.asList(new Parameter("a", Types.STRING)), transformer);
        writer.line("return null;");
        writer.end();
        
        writer.end();
        
        match("/testMethods", w.toString());
    }
    
    @Test
    public void testConstructors() throws IOException{
	writer.beginClass(testType);
	
	writer.beginConstructor(Arrays.asList(new Parameter("a", Types.STRING), new Parameter("b", Types.STRING)), transformer);
	writer.end();
	
	writer.beginConstructor(new Parameter("a", Types.STRING));
	writer.end();
	
	writer.end();
        
        match("/testConstructors", w.toString());
	
    }
    
    @Test
    public void testImports() throws IOException{
	writer.staticimports(Arrays.class);
	
        match("/testImports", w.toString());
    }
    

    @Test
    public void testImports2() throws IOException{
        writer.importPackages("java.lang.reflect","java.util");
        
        match("/testImports2", w.toString());
    }
    
    
    @Test
    public void testSuppressWarnings() throws IOException{
	writer.suppressWarnings("unused");
        writer.privateField(Types.STRING, "test");
        
        match("/testSuppressWarnings", w.toString());
    }
    
}
