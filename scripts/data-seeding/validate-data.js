#!/usr/bin/env node

/**
 * Custody Connect Test Data Validator
 * Validates test data format before sending to API
 */

const fs = require('fs');
const path = require('path');

class DataValidator {
    constructor() {
        this.errors = [];
        this.warnings = [];
    }

    validateEvent(event, index) {
        const eventId = `Event[${index}]`;

        // Required fields validation
        if (!event.type || typeof event.type !== 'string') {
            this.errors.push(`${eventId}: Missing or invalid 'type' field`);
        }

        if (!event.sourceSystem || typeof event.sourceSystem !== 'string') {
            this.errors.push(`${eventId}: Missing or invalid 'sourceSystem' field`);
        }

        if (!event.payload || typeof event.payload !== 'string') {
            this.errors.push(`${eventId}: Missing or invalid 'payload' field`);
        }

        // Type validation
        const validTypes = [
            'CUSTODY_DEPOSIT_SETTLED',
            'WITHDRAWAL_REQUESTED',
            'WITHDRAWAL_FAILED',
            'SETTLEMENT_INSTRUCTED',
            'SETTLEMENT_CONFIRMED',
            'CORPORATE_ACTION_DIVIDEND_POSTED',
            'KYC_STATUS_UPDATED',
            'TRADE_ALLOCATION',
            'LEDGER_ADJUSTMENT',
            'EVIDENCE_ATTACHED'
        ];

        if (event.type && !validTypes.includes(event.type)) {
            this.warnings.push(`${eventId}: Unknown event type '${event.type}'`);
        }

        // Payload JSON validation
        if (event.payload) {
            try {
                const parsed = JSON.parse(event.payload);
                if (typeof parsed !== 'object' || parsed === null) {
                    this.errors.push(`${eventId}: Payload must be a valid JSON object`);
                }
            } catch (e) {
                this.errors.push(`${eventId}: Invalid JSON in payload: ${e.message}`);
            }
        }

        // Source system validation
        if (event.sourceSystem && event.sourceSystem.length > 100) {
            this.warnings.push(`${eventId}: sourceSystem is unusually long (${event.sourceSystem.length} chars)`);
        }
    }

    validateFile(filePath) {
        console.log(`🔍 Validating ${filePath}...`);

        if (!fs.existsSync(filePath)) {
            this.errors.push(`File not found: ${filePath}`);
            return false;
        }

        const content = fs.readFileSync(filePath, 'utf8');
        let data;

        try {
            data = JSON.parse(content);
        } catch (e) {
            this.errors.push(`Invalid JSON format: ${e.message}`);
            return false;
        }

        if (!Array.isArray(data)) {
            this.errors.push('Root element must be an array of events');
            return false;
        }

        console.log(`📊 Found ${data.length} events to validate`);

        data.forEach((event, index) => {
            this.validateEvent(event, index);
        });

        return true;
    }

    printResults() {
        if (this.errors.length > 0) {
            console.log('\n❌ Validation Errors:');
            this.errors.forEach(error => console.log(`   ${error}`));
        }

        if (this.warnings.length > 0) {
            console.log('\n⚠️  Validation Warnings:');
            this.warnings.forEach(warning => console.log(`   ${warning}`));
        }

        if (this.errors.length === 0 && this.warnings.length === 0) {
            console.log('\n✅ All validations passed!');
        }

        console.log(`\n📊 Summary: ${this.errors.length} errors, ${this.warnings.length} warnings`);
    }
}

function main() {
    const args = process.argv.slice(2);

    if (args.length === 0) {
        console.log('🚀 Custody Connect Test Data Validator');
        console.log('====================================');
        console.log('');
        console.log('Usage: node validate-data.js <file1.json> [file2.json] ...');
        console.log('');
        console.log('Examples:');
        console.log('  node validate-data.js mini-dataset-fixed.json');
        console.log('  node validate-data.js *.json');
        console.log('');
        process.exit(1);
    }

    const validator = new DataValidator();
    let allValid = true;

    args.forEach(file => {
        const filePath = path.isAbsolute(file) ? file : path.join(process.cwd(), file);
        const isValid = validator.validateFile(filePath);
        if (!isValid) allValid = false;
    });

    validator.printResults();

    if (!allValid) {
        process.exit(1);
    }
}

// Run if called directly
if (require.main === module) {
    main();
}

module.exports = DataValidator;
