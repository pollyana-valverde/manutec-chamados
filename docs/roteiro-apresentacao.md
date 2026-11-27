# Roteiro da apresentação e do vídeo

Tempo total: **15 minutos** (≈12 de apresentação + 3 de perguntas). A demonstração é o centro.

## Antes de ser chamado

- [ ] Dois aparelhos ou emuladores lado a lado no projetor: **operador** logado em um, **técnico** no outro.
- [ ] Um terceiro aparelho/emulador com o **Técnico 2** para mostrar a recusa do C4 (opcional, mas impressiona).
- [ ] QR da `INJ-03` impresso (Máquinas → INJ-03 → botão QR → Compartilhar para imprimir).
- [ ] Máquinas de exemplo cadastradas e pelo menos 2 chamados **encerrados** antigos na INJ-03 (para o histórico ter duração).
- [ ] Fila do técnico **vazia** no início.
- [ ] Console do Firebase aberto na aba Firestore e na aba Regras. GitHub aberto no quadro e em um PR revisado.
- [ ] Internet testada. Ensaio completo com cronômetro no dia anterior.

## Blocos

| Bloco | Tempo | Quem fala | O que mostrar |
|---|---|---|---|
| O problema | 2 min | Pollyana | Slide 2: máquina parada, telefone, caderno. Pergunta: "quantas vezes a injetora 3 quebrou este mês?" |
| A solução | 1 min | Matheus Rosa Alves | Slide 3: dois perfis, o que cada um faz. |
| Demonstração | 5 min | Vinícius Vieira (operador) e Pedro Henrique dos Reis (técnico) | Roteiro abaixo, sem pular passo. |
| Por dentro | 5 min | Pollyana (Firestore e regras) e Matheus Rosa Alves (GitHub) | Coleções no console, regra barrando, quadro do Projects e um PR revisado. |
| Próximos passos | 2 min | Vinícius Vieira | Slide 6: o que ficou de fora e uma limitação honesta. |
| Perguntas | 3 min | todos | — |

## Demonstração (roteiro mínimo do projeto 01)

Falar em voz alta o critério de aceite de cada passo.

1. **Técnico:** mostrar a fila vazia ("Nenhum chamado aberto").
2. **Operador:** tocar em *Ler QR da máquina* e ler o QR impresso da `INJ-03`.
   → *C2: a máquina já vem preenchida e não pode ser trocada.*
3. **Operador:** tocar em *Abrir chamado* sem prioridade → aparece "Escolha a prioridade" (*NC6*).
   Escolher **Alta**, escrever "Vazamento de óleo hidráulico no cilindro de fechamento, máquina parou com alarme de pressão baixa" e enviar.
4. **Técnico:** o chamado aparece sozinho no topo da fila (*C3 / NC2*). Se der tempo, abrir um de prioridade baixa antes para mostrar a ordenação.
5. **Técnico:** abrir o chamado e tocar em **Assumir** → "Em atendimento" com o nome dele (*C4*).
   **Técnico 2** (se houver): tocar em Assumir no mesmo chamado → "já está em atendimento com…" e nada muda.
6. **Técnico:** girar o celular com a solução digitada pela metade → o texto continua lá (*NC7*).
   Escrever "Troca do anel de vedação do cilindro e reposição de 4 L de óleo ISO VG 68. Testado em 10 ciclos." e **Encerrar** (*C5*).
7. **Técnico:** tocar em *Ver histórico da máquina* → chamados da INJ-03 com a duração de cada reparo e o tempo médio (*C6*).

## Por dentro (5 min)

1. Console → Firestore: mostrar `usuarios` (campo `perfil`), `maquinas/INJ-03` e o chamado recém-encerrado com `abertoEm` e `fechadoEm`.
2. Regra barrando:
   - entrar com `semperfil@techinova.com` → tela "Aguardando liberação";
   - ou, na aba **Regras → Playground**, simular um `update` em `chamados/{id}` com o UID do operador → **negado**.
   Explicar a função `assumindo()`: mesmo sem a transação do app, a regra recusa assumir um chamado que não está mais aberto.
3. GitHub: o quadro com tudo em **Feito**, e um PR com comentário do revisor e `Closes #N`.

## Gravação do vídeo

- Grave a tela dos dois emuladores lado a lado (OBS Studio, ou a gravação de tela do macOS com **Cmd + Shift + 5**).
- Siga a **Demonstração** acima, com narração. De 4 a 6 minutos basta.
- Envie para o YouTube como **Não listado** (ou Drive com "Qualquer pessoa com o link") e cole o link na entrega.
