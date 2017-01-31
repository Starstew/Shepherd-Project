package org.ecocean;

import java.io.Writer;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Enumeration;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

/*
 * An almost entirely static / functional class for printing html UI elements
 * for editing class fields on pages such as occurrence.jsp. Note that this
 * class works only in conjunction with webapp/css/classEditTemplate.css
 * and webapp/javascript/classEditTemplate.js
 */

public class ClassEditTemplate {

  public ClassEditTemplate() {
  }

  public static void invokeObjectMethod(Object obj, String methodName, String valueAsString) throws NoSuchMethodException {
    try {
      Class c = findTypeOfField(obj.getClass(), methodName);

      if (c == Double.class){
        Double dbl = Double.parseDouble(valueAsString);
        Method setter = obj.getClass().getMethod(methodName, Double.class);
        setter.invoke(obj, dbl);
        System.out.println("invokeObjectMethod: just invoked "+methodName+" with value "+dbl);
      }

      if (c == Integer.class){
        Integer in = Integer.parseInt(valueAsString);
        Method setter = obj.getClass().getMethod(methodName, Integer.class);
        setter.invoke(obj, in);
        System.out.println("invokeObjectMethod: just invoked "+methodName+" with value "+in);
      }

      if (c == Boolean.class){
        Boolean bo = Boolean.parseBoolean(valueAsString);
        Method setter = obj.getClass().getMethod(methodName, Boolean.class);
        setter.invoke(obj, bo);
        System.out.println("invokeObjectMethod: just invoked "+methodName+" with value "+bo);
      }

      if (c == String.class){
        Method setter = obj.getClass().getMethod(methodName, String.class);
        setter.invoke(obj, valueAsString);
        System.out.println("invokeObjectMethod: just invoked "+methodName+" with value "+valueAsString);
      }

      if (c == DateTime.class){
        DateTime dt = DateTime.parse(valueAsString);
        Method setter = obj.getClass().getMethod(methodName, DateTime.class);
        setter.invoke(obj, dt);
        System.out.println("invokeObjectMethod: just invoked "+methodName+" with value "+dt);

      }
    } catch (Exception e) {
      System.out.println("invokeObjectMethod: was not able to invoke "+methodName+" with value "+valueAsString);
      e.printStackTrace();
    }
  }

  public static Class findTypeOfField(Class parentClass, String fieldSetterName) throws NoSuchMethodException {
    String fieldGetterName = "get"+fieldSetterName.substring(3);
    Method fieldGetter = parentClass.getMethod(fieldGetterName);
    return fieldGetter.getReturnType();
  }

  public static boolean fieldHasType(Class parentClass, String fieldSetterName, Class candidateType) {
    try {
      Method m = parentClass.getMethod(fieldSetterName, candidateType);
      return true;
    }
    catch (NoSuchMethodException e) {
      return false;
    }
  }

  public static boolean newValEqualsOldVal(Occurrence occ, String getterName, int newVal) {
    try {
      java.lang.reflect.Method getter = occ.getClass().getMethod(getterName);
      int oldVal = (Integer) getter.invoke(occ);
      boolean result = (oldVal == newVal);
      System.out.println("ClassEditTemplate: "+getterName+" = "+oldVal);
      System.out.println("                 : newVal = "+newVal);
      System.out.println("                 : result = "+result);
      return result;
    } catch (Exception e) {
      System.out.println("exception caught");
    } finally {
      return (false);
    }
  }

  // excellent helper function by polygenelubricants on http://stackoverflow.com/a/2560017
  public static String splitCamelCase(String s) {
     return s.replaceAll(
        String.format("%s|%s|%s",
           "(?<=[A-Z])(?=[A-Z][a-z])",
           "(?<=[^A-Z])(?=[A-Z])",
           "(?<=[A-Za-z])(?=[^A-Za-z])"
        ),
        " "
     );
  }


  public static String prettyFieldNameFromGetMethod(Method getMeth) {
    String withoutGet = getMeth.getName().substring(3);
    return splitCamelCase(withoutGet);
  }

  public static String inputElemName(Method getMeth, String classNamePrefix) {
    String fieldName = getMeth.getName().substring(3);
    return ("oldValue-"+classNamePrefix+":"+fieldName);
  }


  public static boolean isDisplayableGetter(Method method) {
    try {
      String methName = method.getName();
      if (!methName.startsWith("get")) return false;
      String fieldName = methName.substring(3);

      Class fieldType = method.getReturnType();

      Method setter = method.getDeclaringClass().getMethod("set"+fieldName, fieldType);
      return true;
    } catch (Exception e) {
      System.out.println(e.toString());
      return false;
    }
  }

  public static void printDateTimeSetterRow(Object obj, javax.servlet.jsp.JspWriter out) throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {
    Method getDateTime = obj.getClass().getMethod("getDateTime");
    String className = obj.getClass().getSimpleName(); // e.g. "Occurrence"
    String classNamePrefix = ""; // e.g. "occ"
    if (className.length()>2) classNamePrefix = className.substring(0,3).toLowerCase();
    else classNamePrefix = className.toLowerCase();

    String printValue;
    if (getDateTime.invoke(obj)==null) printValue = "";
    else {
      DateTime dt = (DateTime) getDateTime.invoke(obj);
      LocalDateTime lt = dt.toLocalDateTime();
      System.out.println("DateTime "+dt+" converted to LocalDateTime "+lt);
      printValue = dt.toString("MM-dd-yyyy HH:mm");
    }
    String fieldName = prettyFieldNameFromGetMethod(getDateTime);
    String inputName = inputElemName(getDateTime, classNamePrefix);

    out.println("<tr data-original-value=\""+printValue+"\">");
    out.println("\t<td>"+fieldName+"</td>");
    out.println("\t<td>");
    // hidden input for setting default va a la http://stackoverflow.com/a/11904956
    //out.println("\t\t<input type=\"hidden\" id=\"datepicker\" />");
    out.println("<input class=\"form-control\" type=\"text\" id=\"datepicker\"");
    out.println("name=\""+inputName+"\" ");
    out.println("value=\""+printValue+"\"");
    out.println("/>");
    out.println("\t</td>");


  }


  public static String getPrefixName(Object obj) {
    return getPrefixName(obj.getClass().getSimpleName());
  }

  public static String getPrefixName(String className) {
    if (className.length()>2) return(className.substring(0,3).toLowerCase());
    else return(className.toLowerCase());
  }

  public static void writeEditableFieldRows(Object obj, String[] fieldNames, javax.servlet.jsp.JspWriter out) {
    for (String fieldName : fieldNames) {
      try {
        writeEditableFieldRow(obj, fieldName, out);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void writeEditableFieldRow(Object obj, String fieldName, javax.servlet.jsp.JspWriter out) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    String getterName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    Method getter = obj.getClass().getMethod(getterName);
    writeEditableFieldRow(obj, getter, out);
  }

  // custom method to replicate a very specific table row format on this page
  public static void writeEditableFieldRow(Object obj, Method getMethod, javax.servlet.jsp.JspWriter out) throws IOException, IllegalAccessException, InvocationTargetException {

    String classNamePrefix = getPrefixName(obj);

    String printValue;
    if (getMethod.invoke(obj)==null) printValue = "";
    else printValue = getMethod.invoke(obj).toString();
    String fieldName = prettyFieldNameFromGetMethod(getMethod);
    String inputName = inputElemName(getMethod, classNamePrefix);

    //System.out.println("writeEditableFieldRow on class "+classNamePrefix+": "+className+" "+printValue+" "+fieldName+" "+inputName);



    out.println("<tr data-original-value=\""+printValue+"\">");
    out.println("\t<td>"+fieldName+"</td>");
    out.println("\t<td>");
    out.println("\t\t<input ");
    out.println("name=\""+inputName+"\" ");
    out.println("value=\""+printValue+"\"");
    out.println("/>");
    out.println("\t</td>");



    out.println("<td class=\"undo-container\">");
    out.println("<div title=\"undo this change\" class=\"undo-button\">&#8635;</div>");
    out.println("</td>");



    out.println("\n</tr>");
  }

  public static void saveUpdatedFields(Object obj, HttpServletRequest request, Shepherd myShepherd) throws NoSuchMethodException {

    String relevantParamPrefix = getPrefixName(obj) + ":";
    System.out.println("ClassEditTemplate: Saving updated fields...");
    Enumeration en = request.getParameterNames();
    while (en.hasMoreElements()) {

      String pname = (String) en.nextElement();
      if (pname.indexOf(relevantParamPrefix) == 0) {
        String setterName = "set" + pname.substring(4,5).toUpperCase() + pname.substring(5);
        String value = request.getParameter(pname);
        invokeObjectMethod(obj, setterName, value);
      }

    }
    myShepherd.commitDBTransaction();
    System.out.println("ClassEditTemplate transaction committed");
  }


  public static void printUnmodifiableField(Object obj, Method getMethod, javax.servlet.jsp.JspWriter out) throws IOException, IllegalAccessException, InvocationTargetException {
    String className = obj.getClass().getSimpleName(); // e.g. "Occurrence"
    String classNamePrefix = ""; // e.g. "occ"
    if (className.length()>2) classNamePrefix = className.substring(0,3).toLowerCase();
    else classNamePrefix = className.toLowerCase();

    String printValue;
    if (getMethod.invoke(obj)==null) printValue = "";
    else printValue = getMethod.invoke(obj).toString();
    String fieldName = prettyFieldNameFromGetMethod(getMethod);

    //System.out.println("printUnmodifiableField on class "+className+" "+printValue+" "+fieldName);



    out.println("\n<tr>");
    out.println("\n\t<td>"+fieldName+"</td>");
    out.println("\n\t<td>"+printValue+"</td>");
    out.println("\n</tr>");
  }




}
