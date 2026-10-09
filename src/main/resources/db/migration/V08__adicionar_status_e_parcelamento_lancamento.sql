-- V08: Adicionar status, valor_pago e suporte a parcelamento em lancamento

ALTER TABLE lancamento ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PENDENTE' NOT NULL;
ALTER TABLE lancamento ADD COLUMN IF NOT EXISTS valor_pago DECIMAL(15,2);
ALTER TABLE lancamento ADD COLUMN IF NOT EXISTS numero_parcela INTEGER;
ALTER TABLE lancamento ADD COLUMN IF NOT EXISTS total_parcelas INTEGER;

-- Atualizar status dos registros existentes baseado no preenchimento de data_pagamento
UPDATE lancamento
SET status = CASE
    WHEN data_pagamento IS NOT NULL AND tipo = 'RECEITA' THEN 'RECEBIDO'
    WHEN data_pagamento IS NOT NULL AND tipo = 'DESPESA' THEN 'PAGO'
    ELSE 'PENDENTE'
END,
valor_pago = CASE
    WHEN data_pagamento IS NOT NULL THEN valor
    ELSE NULL
END;
