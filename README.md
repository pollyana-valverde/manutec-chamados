# Manutec – Chamados de Manutenção

> **TechInova: apps para o chão de fábrica** · Projeto 01 – Chamado de Manutenção
> Desenvolvimento de Aplicativos Móveis · App Android em Java com Firebase Authentication e Cloud Firestore

<p align="center">
  <img src="docs/prints/01-login.png" width="200" alt="Login">
  <img src="docs/prints/03-operador-home.png" width="200" alt="Home do operador">
  <img src="docs/prints/06-fila-tecnico.png" width="200" alt="Fila do técnico">
  <img src="docs/prints/09-historico.png" width="200" alt="Histórico da máquina">
</p>

## Integrantes

| Nome | GitHub | Responsabilidade principal |
|---|---|---|
| Pollyana Valverde | [@pollyana-valverde](https://github.com/pollyana-valverde) | Login e perfis, regras do Firestore, documentação |
| Matheus Rosa Alves | [@MatheusRosaAlves](https://github.com/MatheusRosaAlves) | Cadastro de máquinas e QR, histórico da máquina |
| Vinícius Vieira | [@vinioff](https://github.com/vinioff) | Abertura de chamado pelo QR, encerramento |
| Pedro Henrique dos Reis | [@PedroH6002](https://github.com/PedroH6002) | Fila do técnico, assumir chamado com transação |

## O problema

Quando uma máquina para, o operador avisa o líder, o líder liga para a manutenção e alguém anota num caderno.
O técnico só fica sabendo quando alguém consegue falar com ele, dois técnicos às vezes vão à mesma máquina e,
no fim do mês, ninguém sabe dizer quantas vezes a injetora 3 quebrou nem quanto tempo ela ficou parada.

## A solução

O **Manutec** leva o chamado para o celular. Cada máquina tem um QR colado nela.

| Perfil | O que faz no app |
|---|---|
| **Operador** | Lê o QR da máquina parada e abre o chamado com descrição e prioridade. Acompanha os chamados que abriu. |
| **Técnico de manutenção** | Vê a fila em tempo real (prioridade alta primeiro), assume o chamado, encerra informando o que foi feito, cadastra máquinas, gera os QR e consulta o histórico de cada máquina com a duração dos reparos. |

O perfil de cada pessoa é definido pelo grupo no console do Firebase. O usuário nunca escolhe o próprio perfil.

## Requisitos atendidos

| ID | Requisito | Onde está |
|---|---|---|
| C1 | Cadastro de máquinas com código, nome e setor + QR | `MaquinasActivity`, `CadastroMaquinaActivity`, `QrMaquinaActivity` |
| C2 | Operador lê o QR e abre chamado com descrição e prioridade | `OperadorHomeActivity`, `AbrirChamadoActivity` |
| C3 | Fila do técnico ordenada por prioridade e horário, em tempo real | `TecnicoHomeActivity`, `OrdenacaoChamados` |
| C4 | Assumir chamado com transação (dois técnicos não assumem o mesmo) | `DetalheChamadoActivity#assumir` + regra `assumindo()` |
| C5 | Encerrar com a solução; datas de abertura e fechamento gravadas | `DetalheChamadoActivity#encerrar` |
| C6 | Histórico da máquina com a duração de cada reparo | `HistoricoMaquinaActivity`, `ResumoHistorico` |
| NC1 | Login e cadastro com e-mail e senha, sessão mantida, opção Sair | `LoginActivity`, `CadastroActivity`, `MainActivity`, menu Sair |
| NC2 | Firestore com snapshot listener nas listas principais | todas as listas usam `addSnapshotListener` em `onStart` |
| NC3 | Regras de segurança com controle de perfil | [`firestore.rules`](firestore.rules) |
| NC4 | Perfil em `usuarios/{uid}.perfil`, definido no console | `MainActivity#rotear`, `AguardandoPerfilActivity` |
| NC5 | RecyclerView e estado vazio | `ChamadoAdapter`, `MaquinaAdapter`, `view_estado_vazio.xml` |
| NC6 | Formulários validados em português | `Validacao`, `MensagensErro` |
| NC7 | Girar a tela não apaga o que foi digitado | campos com `id` salvam o estado; nenhuma tela trava a orientação |
| NC8 | README, prints, configuração do Firebase e contas de teste | este arquivo |

**Complementares feitos:** tempo médio de reparo e tempo total parada no histórico da máquina, e compartilhamento da imagem do QR para imprimir.

## Prints das telas

| | | |
|---|---|---|
| ![Login](docs/prints/01-login.png) Login | ![Cadastro](docs/prints/02-cadastro.png) Cadastro | ![Home do operador](docs/prints/03-operador-home.png) Home do operador |
| ![Leitura do QR](docs/prints/04-leitura-qr.png) Leitura do QR | ![Abrir chamado](docs/prints/05-abrir-chamado.png) Abrir chamado | ![Fila do técnico](docs/prints/06-fila-tecnico.png) Fila do técnico |
| ![Detalhe](docs/prints/07-detalhe-assumir.png) Assumir | ![Encerrar](docs/prints/08-encerrar.png) Encerrar | ![Histórico](docs/prints/09-historico.png) Histórico da máquina |
| ![Máquinas](docs/prints/10-maquinas.png) Máquinas | ![QR](docs/prints/11-qr-maquina.png) QR da máquina | ![Estado vazio](docs/prints/12-estado-vazio.png) Estado vazio |

## Tecnologias

- Java 17, Android SDK 36 (mínimo Android 8.0 / API 26), Material Design 3, ViewBinding
- Firebase Authentication (e-mail e senha) e Cloud Firestore (BoM 34)
- ZXing Android Embedded 4.3 para ler e gerar QR Code
- JUnit 4 para os testes de ordenação e de duração

## Como configurar o Firebase

O arquivo `google-services.json` **não está no repositório** (fica no `.gitignore`). Cada integrante baixa o seu.

1. Acesse o [console do Firebase](https://console.firebase.google.com) e crie o projeto (por exemplo `manutec-techinova`). O Google Analytics pode ficar desligado.
2. Em **Configurações do projeto → Seus apps**, adicione um app **Android** com o pacote
   `br.com.techinova.manutec`.
3. Baixe o `google-services.json` e coloque em `app/google-services.json`.
4. **Authentication → Método de login →** ative **E-mail/senha**.
5. **Firestore Database → Criar banco de dados**, no **modo de produção**, local `southamerica-east1 (São Paulo)`.
6. Publique as regras: abra a aba **Regras**, cole o conteúdo de [`firestore.rules`](firestore.rules) e clique em **Publicar**.
   Pela linha de comando: `firebase deploy --only firestore:rules`.
7. Abra o projeto no Android Studio, sincronize o Gradle e rode o app.
8. Crie as contas de teste **pelo próprio app** (botão *Criar conta*). Depois, no console, abra
   `usuarios/{uid}` de cada conta e adicione o campo `perfil` (tipo *string*) com o valor `operador` ou `tecnico`.
9. Entre com a conta de técnico, vá em **Máquinas** e cadastre as máquinas da tabela de dados de exemplo.

> Não há índice composto para criar: as consultas usam só filtros de igualdade e a ordenação da fila é feita no app.

### Modelo de dados

```
usuarios/{uid}        nome, email, perfil ("operador" | "tecnico"), criadoEm
maquinas/{codigo}     codigo, nome, setor, criadoEm
chamados/{id}         maquinaCodigo, maquinaNome, setor, descricao,
                      prioridade ("alta" | "media" | "baixa"), prioridadeOrdem (1 | 2 | 3),
                      status ("aberto" | "em_atendimento" | "encerrado"),
                      abertoPorUid, abertoPorNome, abertoEm,
                      tecnicoUid, tecnicoNome, assumidoEm,
                      solucao, fechadoEm
```

## Contas de teste

| Perfil | E-mail | Senha |
|---|---|---|
| Operador | `operador@techinova.com` | `Manutec@2026` |
| Técnico | `tecnico@techinova.com` | `Manutec@2026` |
| Técnico 2 (para testar a transação) | `tecnico2@techinova.com` | `Manutec@2026` |
| Sem perfil (para mostrar o bloqueio) | `semperfil@techinova.com` | `Manutec@2026` |

## Dados de exemplo (TechInova Autopeças)

| Código | Máquina | Setor |
|---|---|---|
| INJ-03 | Injetora Romi EN 300 t | Injeção plástica |
| INJ-05 | Injetora Haitian 220 t | Injeção plástica |
| PRS-01 | Prensa excêntrica 80 t | Estamparia |
| PRS-04 | Prensa hidráulica 150 t | Estamparia |
| TRN-02 | Torno CNC Romi GL 240 | Usinagem |
| CNC-07 | Centro de usinagem Romi D 800 | Usinagem |
| SLD-01 | Robô de solda MIG | Soldagem |
| CMP-01 | Compressor de parafuso 40 hp | Utilidades |

## Histórias de usuário (ES1)

### C1 – Cadastro de máquinas com QR
**Como** técnico de manutenção, **quero** cadastrar cada máquina com código, nome e setor e gerar o QR dela **para** que o operador abra chamados sem digitar nada.
- **Dado** que estou na tela Nova máquina, **quando** preencho código `INJ-03`, nome e setor e toco em Salvar, **então** a máquina aparece na lista e o app mostra o QR com o código `INJ-03`.
- **Dado** que já existe a máquina `INJ-03`, **quando** tento cadastrar outra com o mesmo código, **então** o app mostra "Já existe uma máquina com este código" e não altera a existente.
- **Dado** que deixo o nome vazio ou uso um código com espaço, **quando** toco em Salvar, **então** o campo mostra a mensagem de erro e nada é gravado.

### C2 – Abrir chamado pelo QR
**Como** operador, **quero** ler o QR da máquina parada e descrever o problema **para** chamar a manutenção sem precisar telefonar.
- **Dado** que leio o QR da `INJ-03`, **quando** a tela Abrir chamado aparece, **então** a máquina já vem preenchida com código, nome e setor, sem opção de troca.
- **Dado** que escolhi a prioridade e escrevi a descrição, **quando** toco em Abrir chamado, **então** o chamado é gravado com status Aberto, meu nome e o horário do servidor, e aparece em Meus chamados.
- **Dado** que não escolhi a prioridade ou a descrição tem menos de 10 caracteres, **quando** toco em Abrir chamado, **então** o app mostra a mensagem do campo e não grava nada.
- **Dado** que leio um QR que não é de máquina cadastrada, **quando** a leitura termina, **então** o app avisa que a máquina não foi encontrada.

### C3 – Fila do técnico em tempo real
**Como** técnico de manutenção, **quero** ver a fila de chamados abertos ordenada por prioridade e horário **para** atender primeiro o que é mais urgente.
- **Dado** que estou com a fila aberta, **quando** um operador abre um chamado em outro aparelho, **então** ele aparece na fila sem eu recarregar a tela.
- **Dado** que existem chamados de prioridade alta e baixa, **quando** vejo a fila, **então** os de prioridade alta vêm primeiro e, na mesma prioridade, o mais antigo vem antes.
- **Dado** que não há chamados abertos, **quando** abro a fila, **então** vejo a tela de estado vazio.

### C4 – Assumir chamado
**Como** técnico de manutenção, **quero** assumir um chamado aberto **para** que outro técnico não vá atender a mesma máquina.
- **Dado** um chamado com status Aberto, **quando** toco em Assumir, **então** o status muda para Em atendimento com o meu nome e ele sai da fila.
- **Dado** um chamado já assumido por outro técnico, **quando** toco em Assumir, **então** o app mostra que ele já está em atendimento e nada muda.

### C5 – Encerrar chamado
**Como** técnico de manutenção, **quero** encerrar o chamado escrevendo o que foi feito **para** deixar registrada a solução e o tempo do reparo.
- **Dado** um chamado em atendimento comigo, **quando** escrevo a solução e confirmo o encerramento, **então** o status vira Encerrado e o app grava a data de fechamento.
- **Dado** que a solução está vazia ou tem menos de 10 caracteres, **quando** toco em Encerrar, **então** o app pede a descrição e não encerra.
- **Dado** um chamado assumido por outro técnico, **quando** abro o detalhe, **então** não aparece a opção de encerrar.

### C6 – Histórico da máquina
**Como** técnico de manutenção, **quero** ver o histórico de chamados de uma máquina com a duração de cada reparo **para** saber quantas vezes ela parou e por quanto tempo.
- **Dado** uma máquina com chamados encerrados, **quando** abro o histórico dela, **então** vejo todos os chamados, do mais recente para o mais antigo, com a duração de cada reparo.
- **Dado** uma máquina sem chamados, **quando** abro o histórico, **então** vejo a tela de estado vazio.

## Regras de segurança (NC3)

Resumo do que [`firestore.rules`](firestore.rules) garante:

- O perfil vem de `usuarios/{uid}.perfil`. O app cria esse documento no cadastro **sem** o campo `perfil` e não consegue alterá-lo depois.
- Conta sem perfil não lê nem grava máquinas e chamados.
- Só **técnico** cadastra máquinas. Só **operador** abre chamados, sempre em nome próprio, com status `aberto`, para uma máquina existente e com horário do servidor.
- O operador só lê os chamados que ele mesmo abriu.
- Só existem duas mudanças possíveis num chamado: `aberto → em_atendimento` (o técnico grava a si mesmo) e `em_atendimento → encerrado` (somente o técnico que assumiu). Ninguém apaga chamados.

**Para a demonstração** ("um usuário tentando fazer algo que não pode e sendo barrado"):
1. Entre com `semperfil@techinova.com`: o app fica em "Aguardando liberação" e não acessa nenhuma lista.
2. No console, aba **Regras → Playground**, simule um `update` em `chamados/{id}` autenticado com o UID do operador: a regra nega.
3. No console, tente editar o campo `perfil` pela simulação com o UID do próprio usuário: a regra nega.

## Organização do trabalho (ES2 e ES3)

- **Quadro:** GitHub Projects com as colunas *A fazer*, *Fazendo* e *Feito*. Cada história é uma issue com responsável.
- **Fluxo:** uma branch por issue (`feature/<nº>-<nome>`), pull request com `Closes #<nº>`, revisão e aprovação por outro integrante antes do merge. Nada de commit direto na `main`.
- O passo a passo do grupo está em [`docs/GUIA-DO-GRUPO.md`](docs/GUIA-DO-GRUPO.md).

## Estrutura do projeto

```
app/src/main/java/br/com/techinova/manutec/
├── model/      Maquina, Chamado, Prioridade, StatusChamado, OrdenacaoChamados, ResumoHistorico
├── ui/         telas (Activities) e adapters das listas
└── util/       Sessao, Validacao, MensagensErro, Formatos, Perfis
app/src/test/   testes de ordenação da fila e de duração dos reparos
firestore.rules regras de segurança
docs/           guia do grupo, roteiro da apresentação, slides, checklist e prints
```

## Como gerar o APK

No Android Studio: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**. O arquivo sai em
`app/build/outputs/apk/debug/app-debug.apk` e é anexado na aba **Releases** do GitHub.

## Limitações e próximos passos

- O cadastro de máquinas não tem edição nem exclusão pelo app.
- Sem foto do defeito (complementar com Cloudinary) e sem filtro da fila por setor.
- Sem notificação push quando um chamado é aberto: o técnico precisa estar com o app aberto.
- Com mais um mês: notificação via Cloud Messaging, foto do defeito, painel de indicadores (MTTR e máquina com mais paradas no mês).
