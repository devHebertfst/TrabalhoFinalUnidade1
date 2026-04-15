# Hub de Apps

Aplicativo Android nativo desenvolvido em Kotlin como trabalho final da unidade. O projeto reúne três funcionalidades em uma única central:

- placar de basquete;
- calculadora básica;
- quiz com categorias, cronômetro, revisão de respostas e histórico local.

## Visão geral

Ao abrir o app, o usuário entra em uma tela central (`MainActivity`) que funciona como hub de navegação. A partir dela, é possível acessar:

- `BasqueteActivity`: controle de pontuação para dois times, diferença no placar, encerramento por quartos, desfazer última jogada e reinício da partida;
- `CalculadoraActivity`: operações aritméticas básicas com interface de botões, backspace, limpeza total e tratamento para divisão por zero;
- fluxo de quiz:
  - `QuizMenuActivity`: entrada para iniciar o quiz ou abrir o histórico;
  - `QuizCategoriasActivity`: seleção de uma ou mais categorias;
  - `QuizActivity`: perguntas aleatórias, tempo por questão, progresso, pontuação e confirmação de saída;
  - `QuizResultadoActivity`: resumo final, percentual, tempo total e revisão das respostas;
  - `QuizHistoricoActivity`: exibição do histórico salvo localmente.

## Funcionalidades

### 1. Central de apps

- tela inicial com navegação entre os três módulos;
- alternância entre tema claro e escuro;
- interface construída com layouts XML.

### 2. Placar de basquete

- marcação de `+1`, `+2` e `+3` pontos para dois times;
- cálculo automático de quem está vencendo;
- botão para desfazer a última pontuação;
- controle de quartos da partida;
- bloqueio das ações ao encerrar o quarto final;
- reinício completo do placar e do estado da partida.

### 3. Calculadora

- operações de soma, subtração, multiplicação e divisão;
- entrada decimal;
- botão de apagar último caractere;
- botão de limpeza total;
- mensagem para tentativa de divisão por zero;
- preservação do estado em mudanças de configuração.

### 4. Quiz

- seleção de múltiplas categorias;
- banco local de perguntas em diferentes níveis de dificuldade;
- sorteio de até 10 perguntas por partida;
- cronômetro de 30 segundos por pergunta;
- contador de tempo total da sessão;
- feedback visual para respostas corretas e incorretas;
- diálogo ao esgotar o tempo;
- revisão completa das respostas ao final;
- salvamento local do histórico das partidas com `SharedPreferences`.

## Categorias do quiz

O quiz atualmente inclui perguntas das seguintes categorias:

- Geografia
- História
- Ciência
- Matemática
- Português
- Esportes
- Tecnologia
- Entretenimento

## Tecnologias utilizadas

- Kotlin
- Android SDK
- AppCompat
- Material Design Components
- ConstraintLayout
- Jetpack Compose habilitado no projeto
- Gradle Kotlin DSL

## Requisitos

- Android Studio atualizado;
- JDK 11;
- Android SDK com `minSdk 29`;
- Gradle Wrapper incluído no repositório.

## Como executar

1. Clone o repositório.
2. Abra a pasta no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Execute o app em um emulador ou dispositivo Android.

Pelo terminal, também é possível gerar a build debug com:

```powershell
.\gradlew.bat assembleDebug
```

## Armazenamento

O histórico do quiz é salvo localmente no dispositivo usando `SharedPreferences`, com limite de até 20 partidas armazenadas.
