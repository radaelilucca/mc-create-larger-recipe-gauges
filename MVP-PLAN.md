# Create Bigger Crafters — plano de MVP

## Estado da implementação — 7 de outubro de 2026

O MVP foi implementado no scaffold para Minecraft 1.21.1, NeoForge 21.1.236 e Create 6.0.10-280:

- Gauges 6×6 e 9×9 registrados, com receitas de upgrade e entrada na aba base do Create.
- Capacidade salva por slot do Factory Gauge, sincronizada com o cliente e preservada ao remover um subpainel ou quebrar o último painel.
- Tela compartilhada do Factory Gauge ampliada conforme a receita selecionada. O padrão shaped mecânico e suas casas vazias são exibidos row-major; a interface usa o padrão gráfico original e os controles originais.
- Busca limitada a Mechanical Crafting, com escolha entre receitas candidatas usando rolagem sobre o botão existente. A rolagem sobre uma célula permite alternar entre os filtros conectados que satisfazem aquele ingrediente.
- O pacote usa o arranjo padrão de crafting do Factory Gauge e segue a conexão/desempacotamento existentes do Create.

Validação de engenharia: `gradlew build` passou; `runClient` carregou Create e o mod; carregamento forçado da classe `FactoryPanelScreen` confirmou que os mixins da tela aplicam sem erro. O layout e a execução do pacote ainda precisam de confirmação manual in-game, inclusive Crushing Wheel pela traseira e salvar/recarregar mundo. Portanto, o build e o boot não contam como comprovação do comportamento visual ou da automação em jogo.

## Objetivo

Adicionar dois novos gauges do Factory Gauge para configurar automações de Mechanical Crafters com padrões maiores que 3×3: um gauge com capacidade de até **6×6** e outro de até **9×9**. O jogador configura uma receita registrada de Mechanical Crafting, envia os ingredientes em um pacote e conecta os crafters pela traseira para que o conjunto produza a receita.

O gauge Factory Gauge original continua com seu comportamento 3×3. As extensões de capacidade e de interpretação de receitas ficam associadas aos dois novos gauges.

## Princípios de produto

- Os novos gauges abrem a **mesma tela do Factory Gauge original**, com os mesmos controles, textos, ícones, cores, tipografia, margens, espaçamentos e interações.
- Não haverá uma segunda tela, controles exclusivos ou uma linguagem visual paralela.
- A única diferença visual funcional é a dimensão da grade apresentada pela mesma interface, conforme a capacidade do gauge: 6×6 ou 9×9. O layout deve acomodar essa dimensão sem mudar o estilo ou a hierarquia da tela original.
- O gauge original preserva sua capacidade e fluxo atuais.
- No MVP, o seletor de receitas considera receitas registradas de **Create Mechanical Crafting**. Receitas vanilla de crafting continuam no fluxo já existente do gauge original.
- A seleção automática precisa respeitar posições vazias do padrão. Espaços vazios não representam ingredientes nem devem impedir o envio do pacote.

## Descoberta técnica já realizada

A inspeção local usou o Create **1.21.1–6.0.10**, encontrado em outros projetos do workspace; não foi necessário baixar outro JAR.

- `AllRecipeTypes.MECHANICAL_CRAFTING` registra o tipo de receita mecânica e ajusta o tamanho máximo do padrão para 9×9.
- O `FactoryPanelScreen` original procura receitas do tipo vanilla `CRAFTING`, portanto não encontra receitas de `MECHANICAL_CRAFTING` pelo fluxo atual.
- A conversão de receita do gauge já representa posições vazias como `ItemStack.EMPTY` e produz uma lista de arranjo que pode ultrapassar nove posições para receitas shaped. Isso é uma base útil para os padrões maiores.
- `FactoryPanelConfigurationPacket` já transporta uma lista variável para o arranjo, e o comportamento do painel persiste o arranjo ativo.
- O handler de desempacotamento do Mechanical Crafter compara a área conectada com o arranjo, ignora células vazias e consome os ingredientes correspondentes. A API pública `UnpackingHandler` permite registrar handlers, mas o fluxo padrão pode já cobrir o caso de uso.
- `PackageItem` limita o pacote padrão a nove tipos distintos de itens. A receita de Crushing Wheel usa três tipos, portanto cabe nesse limite.
- A receita local `create:mechanical_crafting/crushing_wheel` é 5×5 e contém quatro cantos vazios. Seu padrão usa 16 Andesite Alloy, 4 Planks e 1 Stone e produz 2 Crushing Wheels.
- As classes do Factory Gauge são implementações internas do Create, sem uma API pública identificada para substituir a tela ou ampliar sua grade. O plano deve isolar a extensão nos novos gauges e reutilizar a tela existente.

## Escopo funcional do MVP

### Gauges

- Adicionar duas variantes: **Factory Gauge 6×6** e **Factory Gauge 9×9**.
- Cada variante define uma capacidade máxima de padrão. A forma ocupada real da receita escolhida determina as dimensões usadas no arranjo e na montagem dos Mechanical Crafters.
- Assim, por exemplo, uma receita 5×5 como Crushing Wheel pode ser configurada no gauge 6×6; não é necessário preencher seis linhas e seis colunas.
- A receita precisa caber nas dimensões suportadas pelo gauge selecionado.

### Tela e edição

- Reutilizar a tela e os mesmos componentes do Factory Gauge original. Tornar a dimensão da grade um parâmetro derivado do gauge ativo.
- Preservar todos os controles e comportamentos visuais existentes; somente a quantidade e disposição dos slots da grade muda.
- Exibir e permitir edição do padrão selecionado, substituindo o padrão base da receita conforme decisão anterior.
- A edição deve continuar vinculada a receitas registradas de Mechanical Crafting e ser validada contra a receita selecionada. Não criar no MVP um sistema livre de receitas arbitrárias.
- Se mais de uma receita registrada corresponder aos ingredientes configurados, abrir o seletor de receitas, em vez de escolher uma correspondência arbitrária.
- O botão equivalente a **Use Auto Crafter Recipe** só fica disponível quando uma receita mecânica compatível puder ser resolvida para a configuração atual.

### Pacotes e automação

- Serializar e sincronizar o padrão escolhido (dimensões, ingredientes e células vazias) pelo fluxo de configuração existente ou pela extensão mínima necessária.
- Construir um pacote que contenha os ingredientes exigidos, respeitando contagem e tipos distintos aceitos pelo pacote padrão.
- Entregar o pacote a um conjunto conectado de Mechanical Crafters e verificar que o padrão é interpretado na orientação e dimensão corretas.
- Aceitar células vazias da receita: não inserir itens nelas, não contá-las como ingrediente e manter o alinhamento row-major do padrão.
- Permitir a conexão pela traseira dos crafters maiores, como no arranjo automatizado 3×3 existente. O pacote deve alcançar o multiblock e iniciar o crafting.
- Manter estado coerente entre interface, configuração sincronizada, dados salvos e montagem física.

## Fora do escopo do MVP

- Alterar ou ampliar o comportamento/capacidade do Factory Gauge original.
- Receitas vanilla com padrões maiores que 3×3 ou expansão de crafting em geral.
- Receitas arbitrárias sem correspondência a uma receita registrada de Mechanical Crafting.
- Mais de um pacote padrão por receita para exceder o limite de tipos ou quantidades.
- Novas telas, controles ou revisão visual do Factory Gauge.
- Suporte a padrões maiores que 9×9.

## Plano por fases e gates

### Fase 0 — validar a extensão técnica

**Trabalho**

- Confirmar no workspace a versão exata do Create usada pelo projeto e localizar os pontos de registro/configuração dos gauges e do FactoryPanelBehaviour.
- Traçar no código a abertura da tela, criação do pacote, sincronização do arranjo e roteamento para o Mechanical Crafter.
- Validar como a área conectada define largura/altura e como orientação, face traseira e células vazias são interpretadas.
- Verificar se os mecanismos existentes comportam 6×6 e 9×9 sem interceptar o comportamento do gauge original.

**Gate de saída**

- Cada extensão necessária tem um ponto de integração identificado.
- Está demonstrado por código ou protótipo que padrões com dimensões variáveis e células vazias sobrevivem ao caminho gauge → pacote → crafters.
- Bloqueios que exijam mixin ou dependência interna do Create estão documentados antes de expandir a implementação.

### Fase 1 — variantes e roteamento isolado

**Trabalho**

- Registrar os gauges 6×6 e 9×9.
- Associar a cada variante seu limite de grade e comportamento estendido.
- Preservar a rota 3×3 do gauge existente e compartilhar apenas o código/interface necessário.

**Gate de saída**

- Os dois itens aparecem e funcionam como gauges configuráveis.
- O gauge original continua limitado ao comportamento atual.
- A variante define corretamente a capacidade máxima e rejeita receitas grandes demais.

### Fase 2 — interface compartilhada e seleção de receita

**Trabalho**

- Adaptar a tela original para renderizar a grade com dimensão fornecida pelo gauge, preservando literalmente os estilos e controles existentes.
- Carregar receitas do tipo Create Mechanical Crafting para os dois novos gauges.
- Implementar correspondência usando ingredientes e padrão shaped, incluindo posições vazias.
- Mostrar o seletor quando houver múltiplas receitas correspondentes.
- Permitir editar o padrão mantendo validação em relação à receita selecionada.

**Gate de saída**

- Capturas comparativas confirmam a mesma linguagem visual e os mesmos controles da tela original; só a grade muda de capacidade.
- Uma entrada de ingredientes ambígua apresenta opções válidas.
- O botão de uso automático habilita e desabilita conforme a existência de uma correspondência válida.

### Fase 3 — pacote, conexão e execução

**Trabalho**

- Persistir e sincronizar o padrão selecionado e suas dimensões.
- Gerar o pacote com as contagens corretas, sem itens para células vazias.
- Integrar com o desempacotamento padrão ou registrar um handler específico somente se a validação da Fase 0 mostrar que o existente é insuficiente.
- Conectar pela traseira os layouts maiores de Mechanical Crafters e conferir o roteamento do pacote.

**Gate de saída**

- Um pacote chega ao multiblock conectado pela traseira, aplica o padrão correto e produz a saída esperada.
- O fluxo permanece funcional depois de fechar/reabrir a tela e salvar/recarregar o mundo.

### Fase 4 — compatibilidade e validação de MVP

**Trabalho**

- Validar Crushing Wheel 5×5 no gauge 6×6 e no gauge 9×9.
- Validar ao menos uma receita mecânica sem células vazias e uma receita com células vazias.
- Validar dimensões no limite (6×6 e 9×9), orientação/rotação suportada, pacote inválido ou incompleto, inventário cheio e múltiplas receitas candidatas.
- Validar que o gauge vanilla original continua com o mesmo fluxo e apresentação.

**Gate de saída**

- Todos os critérios de aceitação abaixo passam no ambiente de jogo da versão alvo.
- Limitações remanescentes são registradas, sem declarar comprovado o que só foi validado por build ou inspeção de código.

## Critérios de aceitação

1. Existem gauges distintos de capacidade 6×6 e 9×9; ambos configuram receitas registradas de Create Mechanical Crafting.
2. Os novos gauges usam a tela do Factory Gauge original e mantêm os mesmos estilos, controles, textos, ícones e interações. Só a dimensão da grade varia.
3. A Crushing Wheel pode ser reconhecida a partir dos ingredientes compatíveis, mesmo com seus quatro cantos vazios, e seu padrão 5×5 é distribuído nas posições corretas.
4. Se os ingredientes puderem representar mais de uma receita, o jogador escolhe a receita no seletor.
5. O padrão selecionado é editável, substitui a disposição base e continua validado contra uma receita registrada.
6. O pacote contém as contagens necessárias e não tenta preencher posições vazias.
7. Um multiblock de Mechanical Crafters conectado pela traseira recebe o pacote e completa a receita.
8. Padrões acima da capacidade do gauge são recusados com o tratamento de validação da própria interface.
9. A configuração sobrevive à sincronização e ao ciclo de salvar/recarregar.
10. O Factory Gauge original conserva seu comportamento 3×3.

## Riscos e pontos de atenção

- **Dependência de internals do Create:** não foi encontrada API pública para ampliar a tela do Factory Gauge. Manter a extensão estreita e isolada nos novos gauges; revisitar a compatibilidade em cada atualização do Create.
- **Limite do pacote:** o pacote padrão comporta nove tipos distintos; uma receita que exceda esse teto não pode ser suportada no MVP sem outra estratégia.
- **Dimensões e forma física:** a interface deve usar a dimensão real da receita e a capacidade máxima do gauge. É necessário provar que isso coincide com a geometria e orientação do multiblock do Mechanical Crafter.
- **Edição da receita:** a validação de alternativas de ingredientes e a edição de células vazias devem evitar criar padrões que pareçam selecionáveis, mas não correspondam à receita registrada.
- **Interação traseira:** a inspeção de código não substitui a confirmação in-game de que o pacote é aceito pela face traseira no conjunto de crafters.

## Decisões assumidas para este plano

- “6×6” e “9×9” significam capacidade máxima; receitas usam suas dimensões reais.
- O escopo de descoberta automática é somente Create Mechanical Crafting.
- Ambiguidade entre receitas é resolvida por um seletor.
- A grade é editável, mas permanece associada e validada contra uma receita registrada.
- Um pacote padrão atende o MVP; dividir uma receita em vários pacotes fica fora de escopo.

## Evidência local

JAR de referência inspecionado: `copycat-replace/libs/create-1.21.1-6.0.10.jar` (Create 1.21.1–6.0.10), além de cópias locais nos projetos `copycat-grate` e `keybinds-wheel`.

Receita de referência: `data/create/recipe/mechanical_crafting/crushing_wheel.json` dentro do JAR.
