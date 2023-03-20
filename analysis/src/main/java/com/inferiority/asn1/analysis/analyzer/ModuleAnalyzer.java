package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public static final String REGEX_EXTENSION_DEFAULT = "(" + Reserved.EXTENSIBILITY + " " + Reserved.IMPLIED + ")";

    public static final String REGEX_TAG_DEFAULT = "(" +
            Reserved.EXPLICIT + " " + Reserved.TAGS + "|" +
            Reserved.IMPLICIT + " " + Reserved.TAGS + "|" +
            Reserved.AUTOMATIC + " " + Reserved.TAGS + ")";

    public static final String REGEX_EXPORTS = "(" + Reserved.EXPORTS + AbstractAnalyzer.CRLF_LEAST +
            "(" + AbstractAnalyzer.REGEX_IDENTIFIER + Operator.COMMA + "?" + AbstractAnalyzer.CRLF + ")+" +
            Operator.SEMICOLON + ")";

    public static final String REGEX_IMPORT = "(" +
            "(" + AbstractAnalyzer.REGEX_IDENTIFIER + Operator.COMMA + AbstractAnalyzer.CRLF +")*" +
            AbstractAnalyzer.REGEX_IDENTIFIER + AbstractAnalyzer.CRLF_LEAST +
            Reserved.FROM + AbstractAnalyzer.CRLF_LEAST + AbstractAnalyzer.REGEX_IDENTIFIER + ")";
    public static final String REGEX_IMPORTS = "((?=" + Reserved.IMPORTS + ")(" +
            AbstractAnalyzer.REGEX_IDENTIFIER + Operator.COMMA + "?" + AbstractAnalyzer.CRLF + ")+" + Operator.SEMICOLON + ")";

    public static final String REGEX_MODULE =
            AbstractAnalyzer.REGEX_IDENTIFIER + AbstractAnalyzer.CRLF_LEAST +
            Reserved.DEFINITIONS + AbstractAnalyzer.CRLF_LEAST +
            REGEX_TAG_DEFAULT + "?" + AbstractAnalyzer.CRLF +
            REGEX_EXTENSION_DEFAULT + "?" + AbstractAnalyzer.CRLF +
            Operator.ASSIGNMENT + AbstractAnalyzer.CRLF_LEAST +
            Reserved.BEGIN + "[\\s\\S]*" + Reserved.END;

    public Module parse(List<Module> modules, String moduleText) throws AnalysisException {
        Module module = new Module();

        module.setIdentifier(RegexUtil.matcher(AbstractAnalyzer.REGEX_IDENTIFIER, moduleText));
        module.setTagDefault(RegexUtil.matcher(REGEX_TAG_DEFAULT, moduleText));
        module.setExtensionDefault(RegexUtil.matcher(REGEX_EXTENSION_DEFAULT, moduleText));
        module.setExports(RegexUtil.matcherFunc(REGEX_EXPORTS, moduleText, str ->
                Arrays.stream(str.replaceAll(Reserved.EXPORTS, "")
                        .replaceAll(Operator.SEMICOLON, "")
                        .split(Operator.COMMA))
                .map(String::trim)
                .toArray(String[]::new)));
        RegexUtil.matcherConsumer(REGEX_IMPORTS, moduleText, str -> {
            List<Map.Entry<String[], String>> imports = new ArrayList<>();
            while (RegexUtil.matches(REGEX_IMPORT, str)) {
                str = RegexUtil.matcherReplaceConsumer(REGEX_IMPORT, str, s -> {
                    String[] split = s.split(Reserved.FROM);
                    imports.add(new AbstractMap.SimpleEntry<>(
                            Arrays.stream(split[0].split(Operator.COMMA)).map(String::trim).toArray(String[]::new),
                            split[1].trim()));
                });
            }
            module.setImports(imports);
        });
        String moduleBodyText = moduleText
                .substring(moduleText.indexOf(Reserved.BEGIN) + Reserved.BEGIN.length(), moduleText.indexOf(Reserved.END))
                .replaceAll(REGEX_EXPORTS, "")
                .replaceAll(REGEX_IMPORTS, "")
                .trim();
        module.setModuleBodyText(moduleBodyText);
        module.setDefinitions(new LinkedList<>());
        parseModuleBody(modules, module, moduleBodyText);
        return module;
    }

    private void parseModuleBody(List<Module> modules, Module module, String moduleBodyText) throws AnalysisException {
        List<Definition> definitions = module.getDefinitions();
        String t = moduleBodyText;

        while (t != null) {
            t = RegexUtil.matcherReplaceBetweenConsumer(AbstractAnalyzer.REGEX_DEFINITION, t, str -> {
                String text = str.toString().trim();
                String primitiveName = AbstractAnalyzer.getPrimitiveType(text);
                Map.Entry<AbstractAnalyzer, List<Definition>> entry = AbstractAnalyzer.getInstance(modules, module, primitiveName);
                definitions.add(entry.getKey().parse(modules, module, primitiveName, entry.getValue(), text, moduleBodyText));
                log.debug("entity:\n{}", definitions.get(definitions.size() - 1));
            });
        }
    }
}
