# Guia do grupo – como entregar o Manutec

Este guia junta a divisão de tarefas, o fluxo de branches e pull requests (ES3), o quadro (ES2) e o checklist da entrega.

## 1. Divisão sugerida

Cada integrante abre **pelo menos 2 PRs** e revisa **pelo menos 2 PRs de colegas**. O professor confere isso pelo histórico do repositório.

| Issue | História / tarefa | Branch | Abre o PR | Revisa |
|---|---|---|---|---|
| #1 | Login, cadastro e perfis (NC1, NC4, NC6, NC7) | `feature/1-login-perfis` | Pollyana | Pedro Henrique dos Reis |
| #2 | C1 – Cadastro de máquinas com QR | `feature/2-cadastro-maquinas` | Matheus Rosa Alves | Pollyana |
| #3 | C2 – Abrir chamado pelo QR | `feature/3-abrir-chamado-qr` | Vinícius Vieira | Matheus Rosa Alves |
| #4 | C3 – Fila do técnico em tempo real | `feature/4-fila-tecnico` | Pedro Henrique dos Reis | Vinícius Vieira |
| #5 | C4 – Assumir chamado com transação | `feature/5-assumir-chamado` | Pedro Henrique dos Reis | Pollyana |
| #6 | C5 – Encerrar chamado com a solução | `feature/6-encerrar-chamado` | Vinícius Vieira | Pedro Henrique dos Reis |
| #7 | C6 – Histórico da máquina | `feature/7-historico-maquina` | Matheus Rosa Alves | Vinícius Vieira |
| #8 | Regras de segurança do Firestore (NC3) | `feature/8-regras-firestore` | Pollyana | Matheus Rosa Alves |
| #9 | README, prints e documentos da entrega (NC8) | `docs/9-readme-entrega` | Pollyana | Pedro Henrique dos Reis |

Na apresentação, cada um fala pelo menos um bloco (veja [`roteiro-apresentacao.md`](roteiro-apresentacao.md)).
**Qualquer integrante pode ser chamado para explicar qualquer trecho do código**, então todos precisam ler o código das 9 branches, não só o das próprias.

## 2. Preparação (uma vez só)

1. **Repositório:** a dona (Pollyana) adiciona os 3 colegas e o professor em **Settings → Collaborators → Add people**.
2. **Firebase compartilhado:** a dona cria o projeto (passo a passo no README) e adiciona os colegas em
   **Configurações do projeto → Usuários e permissões**. Todos usam o mesmo `google-services.json`, enviado pelo
   WhatsApp ou Drive. **Nunca** faça commit dele.
3. **Quadro (ES2):** no repositório, aba **Projects → New project → Board**. Deixe as colunas **A fazer**, **Fazendo** e **Feito**.
   Em **Add item**, adicione as issues #1 a #9. Em cada issue, preencha **Assignees** com o responsável da tabela acima.

## 3. Ordem dos pull requests

As branches já estão no GitHub, uma em cima da outra (a #2 parte da #1, a #3 parte da #2 e assim por diante).
Por isso a ordem importa:

1. Abra o PR da branch #1 com base `main`.
2. O revisor aprova, o autor faz o merge com **"Create a merge commit"**. **Não use "Squash" nem "Rebase"**: isso faria os próximos PRs mostrarem as alterações repetidas.
3. Só depois do merge, abra o PR da branch seguinte, também com base `main`. Ele vai mostrar apenas a mudança daquela história.
4. Repita até a #9.

### Como abrir o PR (autor)

1. GitHub → **Pull requests → New pull request** → base `main`, compare: a sua branch.
2. Título: o mesmo da issue. Na descrição, use o modelo abaixo e mantenha a linha `Closes #N`.
3. Em **Reviewers**, escolha o revisor da tabela. Em **Assignees**, você mesmo.
4. Mova a issue para **Fazendo** no quadro.

```markdown
## O que foi feito
- ...

## Como testar
1. ...

## Critérios de aceite conferidos
- [ ] Dado ..., quando ..., então ...

Closes #N
```

### Como revisar (revisor)

Revisar não é só clicar em Approve. Antes de aprovar:

1. No Android Studio: **Git → Fetch**, depois troque para a branch do PR (**Git → Branches → origin/... → Checkout**).
2. Rode o app e confira **cada critério de aceite** da issue.
3. Deixe pelo menos um comentário no código (aba **Files changed**, clique no `+` da linha). Pode ser uma pergunta ou uma sugestão.
4. Se algo falhar, use **Request changes** e explique no PR, não pelo WhatsApp.
5. Se tudo funcionar, **Approve**. Depois do merge, a issue fecha sozinha por causa do `Closes #N`. Confira se ela foi para **Feito** no quadro.

### Se o grupo quiser os commits no nome de cada autor

Os commits foram feitos pela conta da Pollyana. Se o grupo preferir que cada PR tenha commits do próprio autor, o
autor pode, antes de abrir o PR, fazer uma melhoria pequena e real na branch (um comentário que explique o código,
uma mensagem de erro melhor, um teste) e enviar um commit dele. Isso também ajuda a entender o código para a arguição.

## 4. APK e Releases

1. Com tudo na `main`, no Android Studio: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
2. GitHub → **Releases → Draft a new release** → tag `v1.0` → título "Manutec 1.0 – entrega final".
3. Arraste o arquivo `app/build/outputs/apk/debug/app-debug.apk` (pode renomear para `manutec-v1.0-debug.apk`) e publique.

## 5. Checklist final da entrega

- [ ] Professor adicionado como colaborador
- [ ] 9 issues fechadas, todas em **Feito** no quadro, e cada uma funcionando na demonstração
- [ ] Cada integrante com PRs abertos e PRs revisados
- [ ] Prints reais em `docs/prints/` (nomes listados no README) e README com os nomes e GitHub dos integrantes
- [ ] Contas de teste criadas e com perfil no console
- [ ] `firestore.rules` publicado no console
- [ ] APK na aba Releases
- [ ] `docs/slides.pdf` (no máximo 6 slides, com prints)
- [ ] `docs/checklist-core.docx` revisado com honestidade
- [ ] Vídeo da demonstração (YouTube não listado ou Drive com acesso liberado)
- [ ] Link do repositório enviado ao professor
