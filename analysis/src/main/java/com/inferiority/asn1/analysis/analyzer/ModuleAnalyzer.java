package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Module;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cuijiufeng
 * @date 2023/2/26 12:10
 * ModuleDefinition ::=
 *      ModuleIdentifier
 *      DEFINITIONS
 *      TagDefault
 *      ExtensionDefault
 *      "::="
 *      BEGIN
 *      ModuleBody
 *      END
 *
 *  ModuleIdentifier ::=
 *      modulereference
 *      DefinitiveIdentifier
 *
 *  DefinitiveIdentifier ::= "{" DefinitiveObjIdComponentList "}" | empty
 *
 *  DefinitiveObjIdComponentList ::=
 *      DefinitiveObjIdComponent | DefinitiveObjIdComponent DefinitiveObjIdComponentList
 *
 *  DefinitiveObjIdComponent ::= NameForm | DefinitiveNumberForm | DefinitiveNameAndNumberForm
 *
 *  DefinitiveNumberForm ::= number
 *
 *  DefinitiveNameAndNumberForm ::= identifier "(" DefinitiveNumberForm ")"
 *
 *  TagDefault ::= EXPLICIT TAGS | IMPLICIT TAGS | AUTOMATIC TAGS | empty
 *
 *  ExtensionDefault ::= EXTENSIBILITY IMPLIED | empty
 *
 *  ModuleBody ::= Exports Imports AssignmentList | empty
 *
 *  Exports ::= EXPORTS SymbolsExported ";" | EXPORTS ALL ";" | empty
 *
 *  SymbolsExported ::= SymbolList | empty
 *
 *  Imports ::= IMPORTS SymbolsImported ";" | empty
 *
 *  SymbolsImported ::= SymbolsFromModuleList | empty
 *
 *  SymbolsFromModuleList ::= SymbolsFromModule | SymbolsFromModuleList SymbolsFromModule
 *
 *  SymbolsFromModule ::= SymbolList FROM GlobalModuleReference
 *
 *  GlobalModuleReference ::= modulereference AssignedIdentifier
 *
 *  AssignedIdentifier ::= ObjectIdentifierValue | DefinedValue | empty
 *
 *  SymbolList ::= Symbol | SymbolList "," Symbol
 *
 *  Symbol ::= Reference | ParameterizedReference
 *
 *  Reference ::= typereference | valuereference | objectclassreference | objectreference | objectsetreference
 */
@Slf4j
public class ModuleAnalyzer {

    public static final String REGEX_EXTENSION_DEFAULT = "(" + Reserved.EXTENSIBILITY + " " + Reserved.IMPLIED + AbstractAnalyzer.CRLF_LEAST + ")";

    public static final Pattern PATTERN_EXTENSION_DEFAULT = Pattern.compile(REGEX_EXTENSION_DEFAULT);

    public static final String REGEX_TAG_DEFAULT = "(" +
            Reserved.EXPLICIT + " " + Reserved.TAGS + AbstractAnalyzer.CRLF_LEAST + "|" +
            Reserved.IMPLICIT + " " + Reserved.TAGS + AbstractAnalyzer.CRLF_LEAST + "|" +
            Reserved.AUTOMATIC + " " + Reserved.TAGS + AbstractAnalyzer.CRLF_LEAST + ")";

    public static final Pattern PATTERN_TAG_DEFAULT = Pattern.compile(REGEX_TAG_DEFAULT);

    public static final String REGEX_EXPORTS = Reserved.EXPORTS + AbstractAnalyzer.CRLF_LEAST +
            "(" + AbstractAnalyzer.PATTERN_IDENTIFIER + AbstractAnalyzer.CRLF + "," + AbstractAnalyzer.CRLF + ")*" +
            AbstractAnalyzer.PATTERN_IDENTIFIER + AbstractAnalyzer.CRLF + ";";

    public static final Pattern PATTERN_EXPORTS = Pattern.compile(REGEX_EXPORTS);

    public static final String REGEX_IMPORTS = Reserved.IMPORTS + AbstractAnalyzer.CRLF_LEAST +
            "(" + AbstractAnalyzer.PATTERN_IDENTIFIER + AbstractAnalyzer.CRLF + ","+ AbstractAnalyzer.CRLF +")*" +
            AbstractAnalyzer.PATTERN_IDENTIFIER + AbstractAnalyzer.CRLF_LEAST +
            "(" + Reserved.FROM + AbstractAnalyzer.CRLF_LEAST +
            "(" + AbstractAnalyzer.PATTERN_IDENTIFIER + AbstractAnalyzer.CRLF + ","+ AbstractAnalyzer.CRLF +")*" +
            AbstractAnalyzer.PATTERN_IDENTIFIER + AbstractAnalyzer.CRLF_LEAST +
            AbstractAnalyzer.CRLF +
            ")*;";

    public static final Pattern PATTERN_IMPORTS = Pattern.compile(REGEX_IMPORTS);

    public static final String REGEX_MODULE =
            AbstractAnalyzer.CRLF +
            AbstractAnalyzer.REGEX_IDENTIFIER + AbstractAnalyzer.CRLF_LEAST +
            Reserved.DEFINITIONS + AbstractAnalyzer.CRLF_LEAST +
            REGEX_TAG_DEFAULT + "?" +
            REGEX_EXTENSION_DEFAULT + "?" +
            Operator.ASSIGNMENT + AbstractAnalyzer.CRLF_LEAST +
            Reserved.BEGIN + "[\\s\\S]*" + Reserved.END +
            AbstractAnalyzer.CRLF;

    public Module parse(String moduleText) throws AnalysisException {
        Module module = new Module();
        Matcher identifierMatcher = AbstractAnalyzer.PATTERN_IDENTIFIER.matcher(moduleText);
        if (!identifierMatcher.find()) {
            throw new AnalysisException("no valid module identifier found");
        }
        module.setIdentifier(identifierMatcher.group());
        Matcher tagDefaultMatcher = PATTERN_TAG_DEFAULT.matcher(moduleText);
        if (tagDefaultMatcher.find()) {
            module.setTagDefault(tagDefaultMatcher.group().trim());
        }
        Matcher extensionDefaultMatcher = PATTERN_EXTENSION_DEFAULT.matcher(moduleText);
        if (extensionDefaultMatcher.find()) {
            module.setExtensionDefault(extensionDefaultMatcher.group().trim());
        }
        Matcher exportsMatcher = PATTERN_EXPORTS.matcher(moduleText);
        if (exportsMatcher.find()) {
            String[] exports = exportsMatcher.group()
                    .replace(Reserved.EXPORTS, "")
                    .replace(Operator.SEMICOLON, "")
                    .split(Operator.COMMA);
            String[] exportArr = Arrays.stream(exports)
                    .map(String::trim)
                    .toArray(String[]::new);
            module.setExports(exportArr);
        }
        Matcher importsMatcher = PATTERN_IMPORTS.matcher(moduleText);
        if (importsMatcher.find()) {
            String imports = importsMatcher.group()
                    .replace(Reserved.IMPORTS, "")
                    .replace(Operator.SEMICOLON, "");
            String[] froms = imports.split(Reserved.FROM);
            String[] importArr = Arrays.stream(froms[0].split(Operator.COMMA))
                    .map(String::trim)
                    .toArray(String[]::new);
            module.setImports(importArr);
            String[] fromArr = Arrays.stream(Arrays.copyOfRange(froms, 1, froms.length))
                    .flatMap(str -> Arrays.stream(str.split(Operator.COMMA)).map(String::trim))
                    .toArray(String[]::new);
            module.setDependencies(fromArr);
        }
        String moduleBodyText = moduleText.substring(moduleText.indexOf(Reserved.BEGIN) + Reserved.BEGIN.length(), moduleText.indexOf(Reserved.END));
        module.setModuleBodyText(moduleBodyText.replaceAll(REGEX_EXPORTS, "").replaceAll(REGEX_IMPORTS, "").trim());
        return module;
    }
}
