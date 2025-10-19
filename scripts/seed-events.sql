-- Seed data for Custody Connect

INSERT INTO event (event_type, payload) VALUES
('CustodyTransfer', '{"transferId": "TR-001", "fromAccount": "ACC-1001", "toAccount": "ACC-1002", "amount": 10000, "currency": "EUR"}'),
('PriceUpdate', '{"securityId": "SEC-0001", "newPrice": 150.50, "timestamp": "2025-10-17T10:00:00Z"}'),
('Deposit', '{"accountId": "ACC-1001", "amount": 5000, "currency": "EUR", "reference": "DEP-123"}'),
('Withdraw', '{"accountId": "ACC-1001", "amount": 2000, "currency": "EUR", "reference": "WIT-456"}'),
('DividendsPaid', '{"securityId": "SEC-0002", "totalAmount": 2500, "paymentDate": "2025-10-16"}'),
('AssetRevaluation', '{"portfolioId": "PORT-001", "totalValue": 150000, "changePercent": 2.5}');

INSERT INTO audit_log (action, entity_type, entity_id, user_id, event_id, details) VALUES
('CREATE', 'EVENT', 1, 'system', 1, 'Event logged by system process'),
('UPDATE', 'EVENT', 2, 'user123', 2, 'Price update approved'),
('DELETE', 'EVENT', 3, 'admin', 3, 'Corrected erroneous deposit');
