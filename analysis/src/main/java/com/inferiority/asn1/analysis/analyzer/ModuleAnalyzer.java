package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            "(" + AbstractAnalyzer.REGEX_IDENTIFIER + AbstractAnalyzer.CRLF + Operator.COMMA + AbstractAnalyzer.CRLF + ")*" +
            AbstractAnalyzer.REGEX_IDENTIFIER + AbstractAnalyzer.CRLF + Operator.SEMICOLON + ")";

    public static final String REGEX_IMPORTS = "(" + Reserved.IMPORTS + AbstractAnalyzer.CRLF_LEAST +
            "(" + AbstractAnalyzer.REGEX_IDENTIFIER + Operator.COMMA + AbstractAnalyzer.CRLF +")*" +
            AbstractAnalyzer.REGEX_IDENTIFIER + AbstractAnalyzer.CRLF_LEAST +
            "(" + Reserved.FROM + AbstractAnalyzer.CRLF_LEAST + AbstractAnalyzer.REGEX_IDENTIFIER + AbstractAnalyzer.CRLF + ")+" +
            Operator.SEMICOLON + ")";

    public static final String REGEX_MODULE =
            AbstractAnalyzer.REGEX_IDENTIFIER + AbstractAnalyzer.CRLF_LEAST +
            Reserved.DEFINITIONS + AbstractAnalyzer.CRLF_LEAST +
            REGEX_TAG_DEFAULT + "?" +
            REGEX_EXTENSION_DEFAULT + "?" + AbstractAnalyzer.CRLF_LEAST +
            Operator.ASSIGNMENT + AbstractAnalyzer.CRLF_LEAST +
            Reserved.BEGIN + REGEX_EXPORTS + "?" + REGEX_IMPORTS + "?" + "[\\s\\S]*" + Reserved.END;

    public Module parse(List<Module> modules, String moduleText) throws AnalysisException {
        Module module = new Module();

        module.setIdentifier(RegexUtil.matcher(AbstractAnalyzer.REGEX_IDENTIFIER, moduleText));
        module.setTagDefault(RegexUtil.matcher(REGEX_TAG_DEFAULT, moduleText));
        module.setExtensionDefault(RegexUtil.matcher(REGEX_EXTENSION_DEFAULT, moduleText));
        module.setExports(RegexUtil.matcherFunction(REGEX_EXPORTS, moduleText,
                str -> Arrays.stream(str.replaceAll(Reserved.EXPORTS, "")
                        .replaceAll(Operator.SEMICOLON, "")
                        .split(Operator.COMMA))
                .map(String::trim)
                .toArray(String[]::new)));
        RegexUtil.matcherConsumer(REGEX_IMPORTS, moduleText, str -> {
            String[] froms = str.replaceAll(Reserved.IMPORTS, "")
                    .replaceAll(Operator.SEMICOLON, "")
                    .split(Reserved.FROM);
            String[] importArr = Arrays.stream(froms[0].split(Operator.COMMA))
                    .map(String::trim)
                    .toArray(String[]::new);
            module.setImports(importArr);
            String[] fromArr = Arrays.stream(Arrays.copyOfRange(froms, 1, froms.length))
                    .flatMap(s -> Arrays.stream(s.split(Operator.COMMA)).map(String::trim))
                    .toArray(String[]::new);
            module.setDependencies(fromArr);
        });
        String moduleBodyText = moduleText
                .substring(moduleText.indexOf(Reserved.BEGIN) + Reserved.BEGIN.length(), moduleText.indexOf(Reserved.END))
                .replaceAll(REGEX_EXPORTS, "")
                .replaceAll(REGEX_IMPORTS, "")
                .trim();
        module.setModuleBodyText(moduleBodyText);
        module.setDefinitions(parseModuleBody(modules, moduleBodyText));
        return module;
    }

    private List<Definition> parseModuleBody(List<Module> modules, String moduleBodyText) throws AnalysisException {
        List<Definition> definitions = new ArrayList<>(16);

        for (int idx = 0, start = 0, end = 0; ; idx = start) {
            end = RegexUtil.end(idx, AbstractAnalyzer.REGEX_DEFINITION, moduleBodyText);
            start = RegexUtil.start(end, AbstractAnalyzer.REGEX_DEFINITION, moduleBodyText);
            if (end == -1) {
                break;
            }
            if (start == -1) {
                String substring = moduleBodyText.substring(idx);
                Definition definition = AbstractAnalyzer.getInstance(modules, Reserved.INTEGER)
                        .parse(Reserved.INTEGER, substring, moduleBodyText);
                log.debug("entity:\n{}", definition);
                definitions.add(definition);
                break;
            }
            String substring = moduleBodyText.substring(idx, start);
            Definition definition = AbstractAnalyzer.getInstance(modules, Reserved.INTEGER)
                    .parse(Reserved.INTEGER, substring, moduleBodyText);
            log.debug("entity:\n{}", definition);
            definitions.add(definition);
        }
        return definitions;
    }
}
