import java.lang.Math.round
//import Checkers.{COLS, IMG, IMG2, PIECE_COL1, PIECE_COL2, ROWS, SQR_COL1, SQR_COL2, SQUARE_SIZE, stage}
import scalafx.Includes._
import scalafx.application
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, Label, RadioButton, ToggleGroup}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, _}
import scalafx.scene.shape.{Circle, Ellipse, Rectangle}
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}

import java.awt.{Dimension, Toolkit}
import scala.collection._
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}

object GUI extends JFXApp {
  val ROWS = 8
  val COLS = 8
  val SQUARE_SIZE = 100
  val SQR_COL1: Color = Color.rgb(0, 0, 0)
  val SQR_COL2: Color = Color.rgb(0, 255, 0)
  val PIECE_COL1: Color = Color.White // + smer
  val PIECE_COL2: Color = Color.rgb(180, 42, 43) // - smer
  val WHITE_MAN: Int = 1
  val BLACK_MAN: Int = -1
  val WHITE_KING: Int = 2
  val BLACK_KING: Int = -2
  val IMG = new Image("orao.jpg", (SQUARE_SIZE / 75.0) * 33, (SQUARE_SIZE / 75.0) * 33, true, false)
  val IMG2 = new Image("beli_orao.png", (SQUARE_SIZE / 75.0) * 37, (SQUARE_SIZE / 75.0) * 37, true, false)

  stage = new JFXApp.PrimaryStage {
    title.value = "Checkers"
    var screenSize : Dimension = Toolkit.getDefaultToolkit.getScreenSize
    width = 918
    height = 845
    scene = new Scene {
      fill = new LinearGradient(
        endX = 0,
        stops = Stops(Black, DarkGrey))

      val fig1: Circle = Circle(120, 240, 50, PIECE_COL1)
      val view = new ImageView(IMG)
      view.x = 120 - IMG.width.value / 2.0
      view.y = 240 - IMG.height.value / 2.0

      val fig2: Circle = Circle(220, 390, 50, PIECE_COL2)
      val view2 = new ImageView(IMG2)
      view2.x = 220 - IMG2.width.value / 2.0
      view2.y = 390 - IMG2.height.value / 2.0

      val fig3: Circle = Circle(120, 540, 50, PIECE_COL1)

      val fig4: Circle = Circle(220, 690, 50, PIECE_COL2)

      val startp: Button = new Button {
        text = "player VS player"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 350
        layoutY = 200
      }
      startp.textFill = Color.DarkRed
      val shape: Circle = Circle(100.0)
      startp.setShape(shape)
      startp.onAction = (e: ActionEvent) => {
        stage = new application.JFXApp.PrimaryStage {
          if(hint.isSelected)
            scene = hintScene
          else
            scene = classicScene
        }
      }

      val nohint: RadioButton = new RadioButton {
        text = "hint off"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 350
        layoutY = 270
      }
      nohint.textFill = Color.White
      nohint.setSelected(true)

      val hint: RadioButton = new RadioButton {
        text = "hint on"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 350
        layoutY = 325
      }
      hint.textFill = Color.White

      val toggle = new ToggleGroup
      toggle.toggles = List(nohint, hint)

      val startc: Button = new Button {
        text = "player VS computer"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 350
        layoutY = 380
      }
      startc.textFill = Color.DarkRed
      startc.setShape(shape)
      startc.onAction = (e: ActionEvent) => {
        stage = new application.JFXApp.PrimaryStage {
          scene = computerScene
        }
      }

      val exit: Button = new Button {
        text = "EXIT"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 700
        layoutY = 700
      }
      exit.textFill = Color.DarkRed
      exit.setShape(shape)
      exit.onAction = (e: ActionEvent) => {
        sys.exit(0)
      }

      val label: Label = new Label {
        text = "In our game modification you can choose between\n regular game " +
          "or game with additonal hint:\n you can put temporary figure anywhere for a move!"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 370
        layoutY = 500
      }

      content = List(new HBox {
        padding = Insets(50, 80, 50, 80)
        children = Seq(
          new Text {
            text = "Welcome to Checkers 2021"
            style = "-fx-font: normal bold 40pt sans-serif"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(Red, DarkRed))
            /*effect = new DropShadow{
            color = DarkGray
            radius = 15
            spread = 0.20
          }*/
          })
      }, startp, nohint, hint, startc, exit, fig1, fig2, view, view2, fig3, fig4, label)

      val hintScene: Scene = new Scene(COLS * SQUARE_SIZE + 100, ROWS * SQUARE_SIZE) {
        val hintGame = new HintGame(3)
        val shape: Circle = Circle(100.0)
        val hintbt: Button = new Button {
          text = "HINT"
          style = "-fx-font: normal bold 15pt sans-serif"

        }
        hintbt.textFill = Color.DarkRed
        hintbt.setShape(shape)
        val lbl1 = new Label(s"${hintGame.hint_num}")
        val lbl2 = new Label(s"${hintGame.hint_num}")
        content = List(new HBox {

        }, new VBox(lbl1, hintbt, lbl2) {
          layoutX = SQUARE_SIZE * COLS + 10
          layoutY = SQUARE_SIZE * ROWS / 2 - 40
        })
        hintbt.onAction = (e: ActionEvent) => {
          if ((hintGame.turn == PIECE_COL1 && hintGame.getHintNumber1 > 0) || (hintGame.turn == PIECE_COL2 && hintGame.getHintNumber2 > 0)
            || (((hintGame.turn == PIECE_COL1 && hintGame.getHintNumber1 == 0) || (hintGame.turn == PIECE_COL2 && hintGame.getHintNumber2 == 0)) && hintGame.hinting)) {
            hintGame.change_hinting()
            lbl1.setText(s"${hintGame.getHintNumber1}")
            lbl2.setText(s"${hintGame.getHintNumber2}")
          }
          else {
            new Alert(AlertType.Error) {
              initOwner(stage)
              title = "Hint information!"
              headerText = "Hello! You don't have anymore hint moves!"
              contentText = "Now you are on your own. Good luck!"
            }.showAndWait()
          }
        }

        def draw_board(): Unit = {
          for (i <- Range(0, ROWS))
            for (j <- Range(0, COLS)) {
              val rectangle = Rectangle(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE)
              if ((i + j) % 2 == 0) {
                rectangle.fill = SQR_COL1
                content.addAll(rectangle)
              }
              else {
                rectangle.fill = SQR_COL2
                content.addAll(rectangle)

              }
            }
        }

        def draw_valid_moves(): Unit = { //Koristi za sve igre, samo postavite odgovarajucu instancu
          //        if(game.selected == null)
          if (hintGame.selected == null)
            return
          var map = hintGame.board.get_valid_moves(hintGame.selected)
          for (i <- Range(0, ROWS * COLS)) {
            if (map(i)._1) {
              val circle = Ellipse((i % ROWS + 0.5) * SQUARE_SIZE, (i / ROWS + 0.5) * SQUARE_SIZE, SQUARE_SIZE / 5, SQUARE_SIZE / 5)
              circle.fill = Color.rgb(20, 20, 200)
              content.addAll(circle)
            }
          }
        }

        def draw(p_board: Board): Unit = {
          for (i <- Range(0, ROWS))
            for (j <- Range(0, COLS)) {
              if (p_board.board(i)(j) != null) {
                val p = p_board.board(i)(j)
                content.addAll(p.draw()._1, p.draw()._2, p.draw()._3)
              }
            }
        }

        def hgupdater(): Unit = {
          draw_board()
          draw(hintGame.board)
          if (hintGame.selected != null) {
            draw_valid_moves()
          }
          if (hintGame.winner() == PIECE_COL1) {
            content = new Label {
              text = "Player 1 won! :D"
            }
          }
          if (hintGame.winner() == PIECE_COL2) {
            content = new Label {
              text = "Player 2 won! :D"
            }
          }
        }

        hgupdater()
        onMousePressed = (me: MouseEvent) => {
          val rws = round(me.y) / SQUARE_SIZE
          val cls = round(me.x) / SQUARE_SIZE
          if (cls < COLS)
            hintGame.select(rws.toInt, cls.toInt)
          hgupdater()
        }

      } // END OF SCENE
      val classicScene: Scene = new Scene(COLS * SQUARE_SIZE, ROWS * SQUARE_SIZE) {
        val game = new Game()

        def draw_board(): Unit = {
          for (i <- Range(0, ROWS))
            for (j <- Range(0, COLS)) {
              val rectangle = Rectangle(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE)
              if ((i + j) % 2 == 0) {
                rectangle.fill = SQR_COL1
                content.addAll(rectangle)
              }
              else {
                rectangle.fill = SQR_COL2
                content.addAll(rectangle)

              }
            }
        }

        def draw_valid_moves(): Unit = {
          if (game.selected == null)
            return
          var map = game.board.get_valid_moves(game.selected)
          for (i <- Range(0, ROWS * COLS)) {
            if (map(i)._1) {
              val circle = Ellipse((i % ROWS + 0.5) * SQUARE_SIZE, (i / ROWS + 0.5) * SQUARE_SIZE, SQUARE_SIZE / 5, SQUARE_SIZE / 5)
              circle.fill = Color.rgb(20, 20, 200)
              content.addAll(circle)
            }
          }
        }

        def draw(p_board: Board): Unit = {
          for (i <- Range(0, ROWS))
            for (j <- Range(0, COLS)) {
              if (p_board.board(i)(j) != null) {
                val p = p_board.board(i)(j)
                content.addAll(p.draw()._1, p.draw()._2, p.draw()._3)
              }
            }
        }

        def gupdater(): Unit = {
          draw_board()
          draw(game.board)
          draw_valid_moves()
          if (game.winner() == PIECE_COL1) {
            content = new Label {
              text = "Player 1 won! :D"
            }
          }
          if (game.winner() == PIECE_COL2) {
            content = new Label {
              text = "Player 2 won! :D"
            }
          }
        } //END OF GUPDATER

        gupdater()
        onMousePressed = (me: MouseEvent) => {
          val rws = round(me.y) / SQUARE_SIZE
          val cls = round(me.x) / SQUARE_SIZE
          if (cls < COLS)
            game.select(rws.toInt, cls.toInt)
          gupdater()
        }

      } // END OF SCENE
      val computerScene: Scene = new Scene(COLS * SQUARE_SIZE + 100, ROWS * SQUARE_SIZE) {
        val computerGame = new ComputerGame(PIECE_COL1)
        val btn: Button = new Button {
          text = "Get Next Move"
          style = "-fx-font: normal bold 7pt sans-serif"
          layoutX = COLS * SQUARE_SIZE + 10
          layoutY = ROWS * SQUARE_SIZE / 2.0 - this.height.value / 2.0
        }
        btn.setShape(Circle(100.0))
        btn.onAction = (e: ActionEvent) => {
          if (computerGame.turn == computerGame.computer_color) {
            var tuple = max(computerGame.board, computerGame, 4, computerGame.computer_color)
            computerGame.board = tuple._1
            computerGame.change_turn()
            //          computerGame.play_computer_move()
            cgupdater()
          }
        }
        content = List(new HBox {}, btn)

        def draw_board(): Unit = {
          for (i <- Range(0, ROWS))
            for (j <- Range(0, COLS)) {
              val rectangle = Rectangle(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE)
              if ((i + j) % 2 == 0) {
                rectangle.fill = SQR_COL1
                content.addAll(rectangle)
              }
              else {
                rectangle.fill = SQR_COL2
                content.addAll(rectangle)

              }
            }
        }

        def draw_valid_moves(): Unit = {
          if (computerGame.selected == null)
            return
          var map = computerGame.board.get_valid_moves(computerGame.selected)
          for (i <- Range(0, ROWS * COLS)) {
            if (map(i)._1) {
              val circle = Ellipse((i % ROWS + 0.5) * SQUARE_SIZE, (i / ROWS + 0.5) * SQUARE_SIZE, SQUARE_SIZE / 5, SQUARE_SIZE / 5)
              circle.fill = Color.rgb(20, 20, 200)
              content.addAll(circle)
            }
          }
        }

        def draw(p_board: Board): Unit = {
          for (i <- Range(0, ROWS))
            for (j <- Range(0, COLS)) {
              if (p_board.board(i)(j) != null) {
                val p = p_board.board(i)(j)
                content.addAll(p.draw()._1, p.draw()._2, p.draw()._3)
              }
            }
        }

        def cgupdater(): Unit = {
          draw_board()
          draw(computerGame.board)
          draw_valid_moves()
          //        if(computerGame.winner() != null){
          //          stage.close()
          //        }
        } //END OF GUPDATER
        def play_computer_game(): Unit = {
          cgupdater()
          onMousePressed = (me: MouseEvent) => {
            if (computerGame.turn != computerGame.computer_color) {
              val rws = round(me.y) / SQUARE_SIZE
              val cls = round(me.x) / SQUARE_SIZE
              if (cls < COLS)
                computerGame.select(rws.toInt, cls.toInt)
              cgupdater()
            }
          }
        }

        play_computer_game()
      } // END OF SCENE
      scene = classicScene

    } //END OF PRIMARY STAGE


    class Board(var board: Array[Array[Piece]], var player1_left: Int, var player2_left: Int, var player1_kings: Int, var player2_kings: Int) {
      def this() {
        this(Array.ofDim[Piece](ROWS, COLS), 12, 12, 0, 0)
        this.create_board()
      }

      def get_piece(i: Int, j: Int): Piece = {
        if (this.board(i)(j) == null)
          return null
        this.board(i)(j)
      }

      def get_pieces(color: Color): List[Piece] = {
        var list: List[Piece] = List()
        for (i <- Range(0, ROWS))
          for (j <- Range(0, COLS))
            breakable {
              val piece = this.get_piece(i, j)
              if (piece == null)
                break
              if (piece.color == color)
                list = list ++ List(piece)
            }
        list
      }

      override def clone(): Board = {
        var pboard = Array.ofDim[Piece](ROWS, COLS)
        for (i <- Range(0, ROWS))
          for (j <- Range(0, COLS)) {
            breakable {
              if (this.get_piece(i, j) == null) {
                pboard(i)(j) = null
                break
              }
              pboard(i)(j) = new Piece(i, j, this.get_piece(i, j).color, this.get_piece(i, j).king)
            }
          }
        val new_board = new Board(pboard, this.player1_left, this.player2_left, this.player1_kings, this.player2_kings)
        new_board
      }

      def move(piece: Piece, row: Int, col: Int): Unit = {
        this.board(row)(col) = this.board(piece.row)(piece.col)
        this.board(piece.row)(piece.col) = null
        piece.move(row, col)
        if (row == 0 || row == ROWS - 1) {
          if (piece.color == PIECE_COL1 && !piece.king)
            this.player1_kings = this.player1_kings + 1
          else if (piece.color == PIECE_COL2 && !piece.king)
            this.player2_kings = this.player2_kings + 1
          piece.make_king()
        }
      }

      def create_board(): Unit = {
        for (i <- Range(0, ROWS))
          for (j <- Range(0, COLS)) {
            if ((i + j) % 2 == 1) {
              if (i < 3) {
                this.board(i)(j) = new Piece(i, j, PIECE_COL1)
              }
              else if (i > ROWS - 4) {
                this.board(i)(j) = new Piece(i, j, PIECE_COL2)
              }
              else
                this.board(i)(j) = null
            }
            else
              this.board(i)(j) = null
          }
      }

      def winner(color: Color): Color = {
        if (ender && color == PIECE_COL1)
          return PIECE_COL2
        if (ender && color == PIECE_COL2)
          return PIECE_COL1
        null
      }

      def remove(pieces: List[Piece]): Unit = {
        for (p <- pieces) {
          this.board(p.row)(p.col) = null
          if (p != null) {
            if (p.getColor() == PIECE_COL1) {
              if (p.king)
                this.player1_kings -= 1
              this.player1_left -= 1
            } else {
              if (p.king)
                this.player2_kings -= 1
              this.player2_left -= 1
            }
          }
        }
      }

      private var moves = Array.ofDim[Tuple2[Boolean, List[Piece]]](ROWS * COLS)
      private var captures: Boolean = false
      private var ender = false

      def setEnder(b: Boolean): Unit = {
        ender = b
      }

      def initialize_moves(): Unit = {
        for (i <- Range(0, ROWS * COLS)) {
          moves(i) = (false, List())
        }
      }

      def capture_priority_and_ender(color: Color): Unit = {
        var ind = false
        ender = true
        for (i <- Range(0, ROWS)) {
          for (j <- Range(0, COLS)) {
            breakable {
              val tmp = this.get_piece(i, j)
              if (tmp == null) {
                break
              }
              if (tmp.color == color) {
                val map = this.get_valid_moves(tmp)
                for (k <- map.indices) {    //k <- Range(0, map.length)
                  if (map(k)._1 && map(k)._2.nonEmpty)   //map(k)._1 && !map(k)._2.isEmpty
                    ind = true
                  if (map(k)._1)
                    ender = false
                }
              }
            }
          }
        }
        captures = ind
      }

      def setCaptures(c: Boolean): Unit = {
        captures = c
      }

      def get_valid_moves(piece: Piece): Array[Tuple2[Boolean, List[Piece]]] = {
        initialize_moves()
        if (piece == null) {
          return moves
        }
        var step = 1
        if (piece.color == PIECE_COL2)
          step = -1
        traverse_left(piece, List(), step)
        traverse_right(piece, List(), step)
        if (piece.king) {
          traverse_left(piece, List(), -step)
          traverse_right(piece, List(), -step)
        }
        //Ukoliko zelite da iskljucite obavezno preskakanje, izbrisite sledece grananje
        if (captures) {
          for (i <- moves.indices) {   //i <- Range(0, moves.length)
            if (moves(i)._1 && moves(i)._2.isEmpty)
              moves(i) = (false, List())
          }
        }
        moves(piece.row * ROWS + piece.col) = (false, List())
        moves    //return moves
      }

      def traverse_left(piece: Piece, skipped: List[Piece], step: Int): Unit = {
        var left = piece.col - 1
        var last: List[Piece] = List()
        var end = -4
        if (step == 1) {
          end = Math.min(ROWS, piece.row + 3)
        } else if (step == -1) {
          end = Math.max(-1, piece.row - 3)
        }
        breakable {
          for (r <- Range(piece.row + step, end, step)) {
            if (left < 0)
              break
            val current = this.get_piece(r, left)
            if (current == null) {
              if (skipped.nonEmpty && last.isEmpty) {
                break
              }
              else if (skipped.nonEmpty) {
                moves(r * ROWS + left) = (true, skipped ++ last)
              } else
                moves(r * ROWS + left) = (true, last)
              if (last.nonEmpty) {
                //ukoliko zelite da omogucite zaustavljanje izmedju skokova, izbrisite red ispod
                moves(piece.row * ROWS + piece.col) = (false, List())
                traverse_left(new Piece(r, left, piece.color, piece.king), skipped ++ last, step)
                traverse_right(new Piece(r, left, piece.color, piece.king), skipped ++ last, step)
                if (piece.king) {
                  traverse_left(new Piece(r, left, piece.color, piece.king), skipped ++ last, -step)
                }
              }
              break
            } else if (current.color == piece.color)
              break
            else
              last = List(current)
            left -= 1
          } //END OF FOR
        }

      }

      def traverse_right(piece: Piece, skipped: List[Piece], step: Int): Unit = {
        var right = piece.col + 1
        var last: List[Piece] = List()
        var end = -4
        // COLOR 1 je + smer
        if (step == 1) {
          end = Math.min(ROWS, piece.row + 3)
        } else if (step == -1) {
          end = Math.max(-1, piece.row - 3)
        }
        breakable {
          for (r <- Range(piece.row + step, end, step)) {
            if (right > ROWS - 1)
              break
            val current = this.get_piece(r, right)
            if (current == null) {
              if (skipped.nonEmpty && last.isEmpty) {
                break
              }
              else if (skipped.nonEmpty) {
                moves(r * ROWS + right) = (true, skipped ++ last)
              } else
                moves(r * ROWS + right) = (true, last)
              if (last.nonEmpty) {
                //ukoliko zelite da omogucite zaustavljanje izmedju skokova, izbrisite red ispod
                moves(piece.row * ROWS + piece.col) = (false, List())
                traverse_left(new Piece(r, right, piece.color, piece.king), skipped ++ last, step)
                traverse_right(new Piece(r, right, piece.color, piece.king), skipped ++ last, step)
                if (piece.king) {
                  traverse_right(new Piece(r, right, piece.color, true), skipped ++ last, -step)
                }
              }
              break
            } else if (current.color == piece.color)
              break
            else
              last = List(current)
            right += 1
          } //END OF FOR
        }
      }

    } // END OF CLASS BOARD

    class Piece(var row: Int, var col: Int, var x: Double, var y: Double, var king: Boolean, var color: Color, val joker: Boolean) {
      //    def apply(row: Nothing, col: Nothing, i: Int, i1: Int, bool: Boolean, color: Nothing): Unit = ???

      def this(row: Int, col: Int, color: Color) {
        this(row, col, 0.0, 0.0, false, color, false)
        this.calc_positions()
      }

      def this(row: Int, col: Int, color: Color, king: Boolean) {
        this(row, col, 0.0, 0.0, king, color, false)
        this.calc_positions()
      }

      def this(row: Int, col: Int, color: Color, king: Boolean, joker: Boolean) {
        this(row, col, 0.0, 0.0, king, color, joker)
        this.calc_positions()
      }

      def calc_positions(): Unit = {
        this.x = this.col * SQUARE_SIZE + SQUARE_SIZE / 2.0
        this.y = this.row * SQUARE_SIZE + SQUARE_SIZE / 2.0
      }

      def make_king(): Unit = {
        this.king = true
      }

      def getColor(): Color = {
        this.color
      }

      def move(row: Int, col: Int) = {
        this.col = col
        this.row = row
        this.calc_positions()
        if (row == 0 || row == ROWS - 1)
          this.make_king()
      }

      def draw(): Tuple3[Node, Node, Node] = {
        val circle1 = Ellipse(this.x, this.y, SQUARE_SIZE / 2 - 7, SQUARE_SIZE / 2 - 7)
        val circle2 = Ellipse(this.x, this.y, SQUARE_SIZE / 2 - 5, SQUARE_SIZE / 2 - 5)
        circle1.fill = this.color
        circle2.fill = Color.Black
        if (this.king && this.color == PIECE_COL1) {
          val view = new ImageView(IMG)
          view.x = this.x - IMG.width.value / 2.0
          view.y = this.y - IMG.height.value / 2.0
          return (circle2, circle1, view)
        }
        if (this.king && this.color == PIECE_COL2) {
          val view = new ImageView(IMG2)
          view.x = this.x - IMG2.width.value / 2.0
          view.y = this.y - IMG2.height.value / 2.0
          return (circle2, circle1, view)
        }
        if (this.joker) {
          val circle3 = Ellipse(this.x, this.y, SQUARE_SIZE / 4, SQUARE_SIZE / 5)
          circle3.fill = Color.rgb(65, 84, 1)
          return (circle2, circle1, circle3)
        }
        (circle2, circle1, circle1)
      }

    } //END OF CLASS PIECE

    class Game(var board: Board, var turn: Color, var selected: Piece) {
      def this() {
        this(new Board(), PIECE_COL1, null)
        this.board.capture_priority_and_ender(this.turn)
      }

      protected var valid_moves = Array.ofDim[Tuple2[Boolean, List[Piece]]](ROWS * COLS)

      def change_turn(): Unit = {
        this.board.setCaptures(false)
        if (this.turn == PIECE_COL1)
          this.turn = PIECE_COL2
        else
          this.turn = PIECE_COL1
        this.board.capture_priority_and_ender(this.turn)
      }

      def move(row: Int, col: Int): Boolean = {
        val tmp = this.board.get_piece(row, col)
        if (tmp == null && this.selected.color == this.turn && valid_moves(row * ROWS + col)._1) {    //tmp == null && this.selected.color == this.turn && valid_moves(row * ROWS + col)._1 == true
          this.board.move(this.selected, row, col)
          //treba ukloniti figurice koje su eventualno pojedene
          if (valid_moves(row * ROWS + col)._2.nonEmpty) {    //valid_moves(row * ROWS + col)._2.isEmpty == false
            this.board.remove(valid_moves(row * ROWS + col)._2)
          }
          this.change_turn()
          return true
        }
        false
      }

      def select(row: Int, col: Int): Unit = {
        if (this.selected != null) {
          val result = this.move(row, col)
          this.selected = null
          if (!result)
            this.select(row, col)
        }
        val piece = this.board.get_piece(row, col)
        if (piece != null && this.turn == piece.color) {
          this.selected = piece
          //uzmi odgovarajuce poteze za selektovanu figuricu
          valid_moves = this.board.get_valid_moves(this.selected)
          return true
        }
        return false
      } //END OF SELECT

      def winner(): Color = {
        this.board.winner(this.turn)
      }
    }

    /* END OF CLASS GAME */

    class ComputerGame(var board: Board, var turn: Color, var selected: Piece, val computer_color: Color) {

      def this(color: Color) {
        this(new Board(), PIECE_COL1, null, color)
      }

      private var lastMove: Tuple4[Piece, Int, Int, List[Piece]] = (null, -1, -1, List())

      private var valid_moves: Array[Tuple2[Boolean, List[Piece]]] = Array.ofDim[Tuple2[Boolean, List[Piece]]](ROWS * COLS)

      def setLastMove(piece: Piece, row: Int, col: Int, list: List[Piece]): Unit = {
        this.lastMove = (piece, row, col, list)
      }


      def getLastMove(): Tuple4[Piece, Int, Int, List[Piece]] = lastMove

      def select(row: Int, col: Int): Unit = {
        if (this.selected != null) {
          val result = this.move(row, col)
          this.selected = null
          if (!result)
            this.select(row, col)
        }
        val piece = this.board.get_piece(row, col)
        if (piece != null && this.turn == piece.color) {
          this.selected = piece
          //uzmi odgovarajuce poteze za selektovanu figuricu
          valid_moves = this.board.get_valid_moves(this.selected)
          return
        }
      }

      def move(row: Int, col: Int): Boolean = {
        val tmp = this.board.get_piece(row, col)
        if (tmp == null && this.selected.color == turn && valid_moves(row * ROWS + col)._1) {    //tmp == null && this.selected.color == turn && valid_moves(row * ROWS + col)._1 == true
          this.board.move(this.selected, row, col)
          //treba ukloniti figurice koje su eventualno pojedene
          if (valid_moves(row * ROWS + col)._2.nonEmpty) {
            this.board.remove(valid_moves(row * ROWS + col)._2)
          }
          this.change_turn()
          return true
        }
        false
      }

      def play_computer_move(): Unit = {
        val list_pieces = this.board.get_pieces(this.computer_color)
        var list_boards: List[Board] = List()
        for (piece <- list_pieces) {
          val map = this.board.get_valid_moves(piece)
          for (k <- map.indices) {    //k <- Range(0, map.length)
            if (map(k)._1) {
              val temp_board = this.board.clone()
              temp_board.move(temp_board.get_piece(piece.row, piece.col), k / ROWS, k % ROWS)
              if (map(k)._2.nonEmpty) {
                temp_board.remove(map(k)._2)
              }
              list_boards = list_boards ++ List(temp_board)
            }
          }
        }

        val rand = new Random()
        this.board = list_boards(rand.nextInt(list_boards.length))
        this.change_turn()
      }

      def change_turn(): Unit = {
        if (this.turn == PIECE_COL1)
          this.turn = PIECE_COL2
        else {
          this.turn = PIECE_COL1
        }
        this.board.setEnder(false)
        this.board.capture_priority_and_ender(this.turn)
      }

      def winner(): Color = {
        this.board.winner(this.turn)
      }

    }

    /*END OF CLASS COMPUTERGAME*/

    class HintGame(var board: Board, var turn: Color, var selected: Piece, var hint_num: Int, var hinting: Boolean) {
      def this(num: Int) {
        this(new Board(), PIECE_COL1, null, num, false)
      }

      def winner(): Color = {
        this.board.winner(this.turn)
      }


      private var valid_moves = Array.ofDim[Tuple2[Boolean, List[Piece]]](ROWS * COLS)
      private var move_ender: Boolean = false
      private var hint_number1: Int = hint_num
      private var hint_number2: Int = hint_num

      def getHintNumber1: Int = hint_number1

      def getHintNumber2: Int = hint_number2

      def change_hinting(): Unit = {
        if (!move_ender) {
          if (this.hinting) {
            this.hinting = false
            if (this.turn == PIECE_COL1)
              hint_number1 = hint_number1 + 1
            else
              hint_number2 = hint_number2 + 1
          } else if (this.turn == PIECE_COL1 && hint_number1 > 0) {
            this.hinting = true
            hint_number1 = hint_number1 - 1
          } else if (this.turn == PIECE_COL2 && hint_number2 > 0) {
            this.hinting = true
            hint_number2 = hint_number2 - 1
          }
        } else
          this.change_turn()
      }

      def remove_hints(color: Color): Unit = {
        val list: List[Piece] = this.board.get_pieces(color)
        for (p <- list)
          if (p.joker)
            this.board.remove(List(p))
      }

      def change_turn(): Unit = {
        this.board.setCaptures(false)
        this.hinting = false
        move_ender = false
        if (this.turn == PIECE_COL1) {
          this.remove_hints(PIECE_COL2)
          this.turn = PIECE_COL2
        } else {
          this.remove_hints(PIECE_COL1)
          this.turn = PIECE_COL1
        }
        this.board.capture_priority_and_ender(this.turn)
      }

      def setJoker(row: Int, col: Int): Unit = {
        this.board.board(row)(col) = new Piece(row, col, this.turn, false, true)
      }

      def select(row: Int, col: Int): Unit = {
        if (this.selected != null && !move_ender) {
          val result = this.move(row, col)
          this.selected = null
        }
        val piece = this.board.get_piece(row, col)
        if (piece == null && this.hinting && move_ender && (row + col) % 2 == 1) {
          setJoker(row, col)
          change_turn()
        }
        if (piece != null && this.turn == piece.color && !piece.joker && !move_ender) {
          this.selected = piece
          //uzmi odgovarajuce poteze za selektovanu figuricu
          valid_moves = this.board.get_valid_moves(this.selected)
          return true
        }
        return false
      } //END OF SELECT

      def move(row: Int, col: Int): Boolean = {
        if (!move_ender) {
          val tmp = this.board.get_piece(row, col)
          if (tmp == null && this.selected.color == turn && valid_moves(row * ROWS + col)._1) {
            this.board.move(this.selected, row, col)
            //treba ukloniti figurice koje su eventualno pojedene
            if (valid_moves(row * ROWS + col)._2.nonEmpty) {
              this.board.remove(valid_moves(row * ROWS + col)._2)
            }
            move_ender = true
            if (!this.hinting)
              change_turn()
            return true
          }
        }
        false
      }

    }

    /*END OF CLASS HINTGAME*/

    def max(board: Board, game: ComputerGame, depth: Int, color: Color): Tuple2[Board, Int] = {
      if (depth == 0 || board.winner(color) != null)
        return (board.clone(), evaluation(board, game, color))
      var current_max = Integer.MIN_VALUE
      var current_best: Board = new Board()
      var other_color = PIECE_COL1
      if (other_color == color)
        other_color = PIECE_COL2
      var tuple: Tuple2[Board, Int] = (null, 0)
      for (state <- get_next_states(board, color, game)) {
        tuple = min(state, game, depth - 1, other_color)
        if (tuple._2 >= current_max) {
          current_max = tuple._2
          current_best = state
        }
      }
      (current_best, current_max)
    }

    def min(board: Board, game: ComputerGame, depth: Int, color: Color): Tuple2[Board, Int] = {
      if (depth == 0 || board.winner(color) != null)
        return (board.clone(), evaluation(board, game, color))
      var current_min = Integer.MAX_VALUE
      var current_best: Board = new Board()
      var other_color = PIECE_COL1
      if (other_color == color)
        other_color = PIECE_COL2
      var tuple: Tuple2[Board, Int] = (null, 0)
      for (state <- get_next_states(board, color, game)) {
        tuple = max(state, game, depth - 1, other_color)
        if (tuple._2 <= current_min) {
          current_min = tuple._2
          current_best = state
        }
      }
      (current_best, current_min)
    }

    def get_next_states(board: Board, color: Color, game: ComputerGame): List[Board] = {
      var list_pieces = board.get_pieces(color)
      var list_boards: List[Board] = List()
      for (piece <- list_pieces) {
        val map = board.get_valid_moves(piece)
        for (k <- map.indices) {    //k <- Range(0, map.length)
          if (map(k)._1) {
            val temp_board = board.clone()
            temp_board.move(temp_board.get_piece(piece.row, piece.col), k / ROWS, k % ROWS)
            if (map(k)._2.nonEmpty) {
              for (r <- map(k)._2)
                temp_board.remove(List(temp_board.get_piece(r.row, r.col)))
            }
            var other_color = PIECE_COL1
            if (other_color == color)
              other_color = PIECE_COL2
            temp_board.capture_priority_and_ender(other_color)
            list_boards = list_boards ++ List(temp_board)
          }
        }
      }
      list_boards
    }

    def evaluation(board: Board, game: ComputerGame, color: Color): Int = {
      board.capture_priority_and_ender(color)
      if (board.winner(color) != null) {
        if (board.winner(color) == game.computer_color)
          return 1000
        if (board.winner(color) != game.computer_color)
          return -1000
      }
      var coef = -1
      if (game.computer_color == PIECE_COL1)
        coef = 1
      coef * (board.player1_kings * 10 + board.player1_left - board.player2_kings * 10 - board.player2_left)
    }

  } //END OF APP
}
