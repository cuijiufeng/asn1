package com.inferiority.asn1.analysis.analyzer;

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
public class MoudleAnalyzer {
}
