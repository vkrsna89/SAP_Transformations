# SAP_Transformations
Groovy Scripts to transform the JSON data to XML or CSV etc
# SAP CPI Utility Scripts: Advanced JSON to XML Transformation

Production-grade Groovy script pattern for SAP Cloud Integration (CPI) / SAP BTP Integration Suite.

## Key Architecture Benefits
- **Memory Efficient:** Uses `StreamingMarkupBuilder` instead of standard `MarkupBuilder` to reduce heap memory overhead during XML generation.
- **Null-Safe:** Handles missing JSON keys and empty arrays `[]` gracefully without breaking downstream XML schemas or BAPI interfaces.
- **Enterprise Ready:** Applies explicit namespace binding (`http://sap.com/demo/orders`) and wraps exceptions cleanly for SAP CPI Exception Sub-Processes.

## Usage in SAP CPI
1. Add a **Groovy Script** step after your HTTP/REST Receiver or Content Modifier.
2. Paste `JsonToXmlTransformer.groovy` into your script bundle.
3. Add an **XSD Validator** node immediately after this script to enforce schema compliance before calling backend SAP S/4HANA or ECC systems.