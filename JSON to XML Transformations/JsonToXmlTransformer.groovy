import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper
import groovy.xml.StreamingMarkupBuilder

/**
 * SAP CPI Groovy Script: Advanced JSON to XML Transformation
 * 
 * Purpose: Transforms inbound JSON payloads into compliant XML structures.
 * Features:
 * - Dynamic root node and namespace injection
 * - Safe handling of null values and empty arrays
 * - Stream-based processing to manage memory footprint efficiently
 * 
 * Author: Senior SAP CPI / Integration Engineer
 */
Message processData(Message message) {
    try {
        // 1. Retrieve inbound message body as String
        def body = message.getBody(String)
        if (!body || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Inbound JSON payload is null or empty.")
        }

        // 2. Parse JSON using JsonSlurper
        def jsonSlurper = new JsonSlurper()
        def jsonPayload = jsonSlurper.parseText(body)

        // 3. Define Target XML Namespace and Root Element
        def targetNamespace = "http://sap.com/demo/orders"

        // 4. Build XML using StreamingMarkupBuilder for memory efficiency
        def xmlBuilder = new StreamingMarkupBuilder()
        xmlBuilder.encoding = "UTF-8"

        def xmlContent = xmlBuilder.bind {
            mkp.xmlDeclaration()
            // Bind root element with namespace alias
            ns1(targetNamespace)
            'ns1:OrderRequest' {
                
                // Map Primitive Fields safely
                'orderId'(jsonPayload.orderId ?: '')
                'customerNumber'(jsonPayload.customerId ?: '')
                
                // Process Nested Objects
                if (jsonPayload.headerDetails) {
                    'headerDetails' {
                        'orderDate'(jsonPayload.headerDetails.orderDate ?: '')
                        'currency'(jsonPayload.headerDetails.currencyCode ?: 'USD')
                    }
                }

                // Process Arrays safely with null/empty checks
                'lineItems' {
                    if (jsonPayload.items && !jsonPayload.items.isEmpty()) {
                        jsonPayload.items.each { item ->
                            'item' {
                                'itemNumber'(item.itemNo ?: '')
                                'materialNumber'(item.materialId ?: '')
                                'quantity'(item.qty ?: 0)
                                'unitOfMeasure'(item.uom ?: 'EA')
                            }
                        }
                    }
                }
            }
        }

        // 5. Set transformed XML payload back to the Message Body
        message.setBody(xmlContent.toString())

        // 6. Optional: Set Exchange Property to signal success
        message.setProperty("TransformationStatus", "SUCCESS")

        return message

    } catch (Exception e) {
        // Raise explicit error to trigger CPI Exception Sub-Process
        throw new Exception("Error during JSON to XML Groovy Transformation: " + e.getMessage(), e)
    }
}
