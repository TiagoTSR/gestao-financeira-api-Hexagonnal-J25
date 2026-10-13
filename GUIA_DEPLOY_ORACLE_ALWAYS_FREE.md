# 🚀 Guia Completo: Deploy Gratuito na Oracle Cloud (Always Free)

Este guia ensina o passo a passo para colocar a **API de Gestão Financeira (Java 25 + PostgreSQL + RabbitMQ)** em produção real **sem nenhum custo financeiro**, utilizando a infraestrutura **Always Free da Oracle Cloud (OCI)**.

---

## 🌟 O que a Oracle oferece de graça para sempre (Always Free)?

* **Processador:** Ampere Altra (ARM64) com até **4 OCPUs (núcleos dedicados)**.
* **Memória RAM:** **24 GB de RAM** (compartilhados entre até 4 instâncias ou concentrados em 1 super máquina).
* **Armazenamento:** **200 GB** de Block Storage SSD NVMe.
* **Rede:** 1 IP Público IPv4 fixo/reservado + 10 TB/mês de tráfego de saída gratuito.

---

## 📋 Passo a Passo

### Passo 1: Criar a Conta na Oracle Cloud (OCI)
1. Acesse: [https://www.oracle.com/cloud/free/](https://www.oracle.com/cloud/free/)
2. Clique em **"Start for free"**.
3. **Dicas Importantes no Cadastro:**
   * **Home Region:** Escolha `Brazil East (Sao Paulo)` ou `Brazil Southeast (Vinhedo)`. Se os recursos ARM estiverem temporariamente esgotados no Brasil, escolha `US East (Ashburn)`.
   * **Cartão de Crédito:** É necessário informar um cartão internacional apenas para validação de identidade (a Oracle faz uma cobrança temporária de ~$1 USD e estorna imediatamente). Você **nunca será cobrado** a menos que faça upgrade manual explícito para "Pay As You Go".

---

### Passo 2: Criar a Instância Compute (Ampere A1)
1. No menu lateral do console da Oracle, vá em: **Compute > Instances** (Computação > Instâncias).
2. Clique em **"Create instance"** (Criar Instância).
3. **Configurações:**
   * **Nome:** `gestao-financeira-prod`
   * **Image and shape (Imagem e Forma):**
     * Clique em **Change Image**: Selecione **Ubuntu 24.04** ou **Canonical Ubuntu Minimal**.
     * Clique em **Change Shape**: Selecione **Ampere (Arm-based)** > `VM.Standard.A1.Flex`.
     * Configure os seletores:
       * **OCPUs:** `4`
       * **Memória:** `24 GB`
   * **Networking:** Deixe a VCN padrão criada e certifique-se de marcar:
     * **Assign a public IPv4 address:** `Yes` (Atribuir IP público).
   * **SSH Keys (Chaves SSH):**
     * Selecione **"Generate a key pair for me"** e clique em **"Save private key"** (baixe o arquivo `.key` no seu PC — você precisará dele para acessar o servidor).
4. Clique em **"Create"**. Aguarde 1 a 2 minutos até o status ficar verde (**Running**).
5. Anote o **Public IP Address** (ex: `129.148.xxx.xxx`).

---

### Passo 3: Liberar as Portas no Firewall da Oracle (Security List)
Por padrão, a Oracle bloqueia todo tráfego de entrada, exceto a porta SSH (22). Precisamos liberar a porta da API (8080) e do RabbitMQ Management (15672):

1. Na tela da instância, clique na sua rede em **Subnet: subnet-...**.
2. Clique em **Default Security List for...**.
3. Clique em **Add Ingress Rules** (Adicionar Regras de Entrada):
   * **Source CIDR:** `0.0.0.0/0`
   * **IP Protocol:** `TCP`
   * **Destination Port Range:** `8080,15672,5432`
   * **Description:** `API Gestao Financeira, RabbitMQ e Postgres`
4. Clique em **Add Ingress Rules**.

---

### Passo 4: Conectar via SSH e Configurar o Servidor

No terminal do seu computador (PowerShell), acerte as permissões da chave SSH e conecte:

```powershell
# No PowerShell (onde salvou sua chave privada):
ssh -i "caminho\para\sua-chave.key" ubuntu@<IP_PUBLICO_ORACLE>
```

Assim que estiver conectado dentro do servidor Ubuntu, execute os comandos abaixo para atualizar o sistema e liberar o firewall interno do Linux (o Ubuntu da Oracle vem com `iptables` ativo):

```bash
# 1. Atualizar pacotes
sudo apt update && sudo apt upgrade -y

# 2. Liberar portas no iptables do Ubuntu da Oracle
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 8080 -j ACCEPT
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 15672 -j ACCEPT
sudo netfilter-persistent save || true

# 3. Instalar Docker e Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu

# Recarrega a sessão do usuário para usar docker sem sudo:
newgrp docker
```

---

### Passo 5: Clonar o Projeto e Subir em Produção

Ainda dentro do servidor da Oracle:

```bash
# 1. Clonar seu repositório Git
git clone https://github.com/TiagoTSR/gestao-financeira-api-Hexagonnal-J25.git
cd gestao-financeira-api-Hexagonnal-J25

# 2. Subir todos os serviços com o Docker Compose de Produção
docker compose -f docker-compose.prod.yml up -d --build
```

Acompanhe os logs da inicialização:
```bash
docker compose -f docker-compose.prod.yml logs -f gestao-api
```

Quando aparecer `Started GestaoFinanceiraApiHexagonalJ25Application in X.XXX seconds`, seus 3 serviços estarão rodando:
* **API Spring Boot:** `http://<IP_PUBLICO_ORACLE>:8080`
* **Painel RabbitMQ:** `http://<IP_PUBLICO_ORACLE>:15672` (login: `gestao_admin` / senha: `gestao_rabbit_prod_2026`)
* **PostgreSQL:** Tunado para 200 conexões com 2GB de cache.

---

### Passo 6: Rodar o Teste de 1.000 Usuários Contra a Nuvem Real

No seu computador local (Windows), abra o PowerShell e dispare o script apontando diretamente para o IP público da sua máquina na Oracle:

```powershell
.\executar-teste-estresse.ps1 -ApiUrl "http://<IP_PUBLICO_ORACLE>:8080"
```

### 🎯 O que esperar do resultado na Oracle Cloud:
* **Contenção ZERO de CPU local:** A máquina na nuvem possui 24GB de RAM e 4 núcleos dedicados só para processar a API e os relatórios assíncronos.
* **Latência Reduzida:** A latência média deve cair de ~9s para valores próximos do tempo de ida e volta da rede (RTT).
* **Taxa de Falha:** Permanecerá cravada em **0.00%**.
