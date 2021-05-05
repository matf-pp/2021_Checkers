import java.lang.Math.round

import scalafx.Includes._
import scalafx.application
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, RadioButton, ToggleGroup}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, _}
import scalafx.scene.shape.{Circle, Ellipse, Rectangle}
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}

import scala.collection._
import scala.util.control.Breaks.{break, breakable}

object GUI extends JFXApp {
  val ROWS = 8
  val COLS = 8
  val SQUARE_SIZE = 100
  val SQR_COL1 = Color.rgb(0, 0, 0)
  val SQR_COL2 = Color.rgb(0, 255, 0)
  val PIECE_COL1 = Color.White // + smer
  val PIECE_COL2 = Color.rgb(180, 42, 43) // - smer
  val WHITE_MAN = 1
  val BLACK_MAN = -1
  val WHITE_KING = 2
  val BLACK_KING = -2
  val IMG = new Image("orao.jpg", (SQUARE_SIZE / 75.0) * 33, (SQUARE_SIZE / 75.0) * 33, true, false)
  val IMG2 = new Image("beli_orao.png", (SQUARE_SIZE / 75.0) * 37, (SQUARE_SIZE / 75.0) * 37, true, false)

  stage = new JFXApp.PrimaryStage {
    title.value = "Checkers"
    width = 918
    height = 845
    scene = new Scene {
      fill = new LinearGradient(
        endX = 0,
        stops = Stops(Black, DarkGrey))

      val img = new Image("orao.jpg", 300.0, 250.0, true, false)
      val view = new ImageView(img)
      view.layoutX = 50
      view.layoutY = 290

      val startp: Button = new Button {
        text = "player VS player"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 400
        layoutY = 190
      }
      startp.textFill = Color.DarkRed
      val shape = Circle(100.0)
      startp.setShape(shape)
      startp.onAction = (e: ActionEvent) => {
        stage = new application.JFXApp.PrimaryStage {
          scene = startscene
        }
      }

      val nohint = new RadioButton {
        text = "hint off"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 400
        layoutY = 250
      }
      nohint.textFill = Color.White
      nohint.setSelected(true)

      val hint = new RadioButton {
        text = "hint on"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 400
        layoutY = 295
      }
      hint.textFill = Color.White

      val toggle = new ToggleGroup
      toggle.toggles = List(nohint, hint)

      val startc = new Button {
        text = "player VS computer"
        style = "-fx-font: normal bold 15pt sans-serif"
        layoutX = 400
        layoutY = 350
      }
      startc.textFill = Color.DarkRed
      startc.setShape(shape)
      startc.onAction = (e: ActionEvent) => {
        stage = new application.JFXApp.PrimaryStage {
          scene = startscene
        }
      }

      val exit = new Button {
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
      }, startp, nohint, hint, startc, exit, view)

      var startscene = new Scene(COLS * SQUARE_SIZE, ROWS * SQUARE_SIZE) {
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

        val hintbt = new Button {
          text = "HINT"
          style = "-fx-font: normal bold 15pt sans-serif"
          layoutX = 820
          layoutY = 400
        }
        hintbt.textFill = Color.DarkRed
        hintbt.setShape(shape)
        content = List(new HBox {

        }, hintbt)


        def draw_pieces(): Unit = {
          for (i <- Range(0, ROWS))
            for (j <- Range(0, COLS)) {
              if ((i + j) % 2 == 1) {
                if (i < 3) {
                  val circle = Ellipse((j + 0.5) * SQUARE_SIZE, (i + 0.5) * SQUARE_SIZE, SQUARE_SIZE / 2 - 10, SQUARE_SIZE / 2 - 10)
                  circle.fill = PIECE_COL1
                  content.addAll(circle)
                }

                if (i > 4) {
                  val circle = Ellipse((j + 0.5) * SQUARE_SIZE, (i + 0.5) * SQUARE_SIZE, SQUARE_SIZE / 2 - 10, SQUARE_SIZE / 2 - 10)
                  circle.fill = PIECE_COL2
                  content.addAll(circle)
                }
              }
            }
        }

        var game = new Game()
        var computerGame = new ComputerGame(PIECE_COL1)

        hint.selected.onChange {
          hintbt.onAction = (e: ActionEvent) => {
            if (Hint.number > 0) {
              all_valid_moves()
              Hint.number = Hint.number - 1
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
        }

        def draw_new_piece(rws: Int, cls: Int): Unit = {
          val circle = Ellipse((cls + 0.5) * SQUARE_SIZE, (rws + 0.5) * SQUARE_SIZE, SQUARE_SIZE / 2 - 10, SQUARE_SIZE / 2 - 10)
          circle.fill = game.turn
          content.add(circle)
        }

        def all_valid_moves(): Unit = {
          for (i <- Range(0, ROWS))
            for (j <- Range(0, COLS)) {
              if ((i + j) % 2 == 1) {
                if (game.board.get_piece(i, j) == null) {
                  val circle = Ellipse((j + 0.5) * SQUARE_SIZE, (i + 0.5) * SQUARE_SIZE, SQUARE_SIZE / 5, SQUARE_SIZE / 5)
                  circle.fill = Color.rgb(20, 20, 200)
                  content.addAll(circle)
                }
              }
            }
        }

        def draw_valid_moves(): Unit = { //Koristi za sve igre, samo postavite odgovarajucu instancu
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
                //              val circle = Ellipse((j + 0.5) * SQUARE_SIZE, (i + 0.5) * SQUARE_SIZE, SQUARE_SIZE / 2 - 10, SQUARE_SIZE / 2 - 10)
                //              circle.fill = p_board.board(i)(j).color
                val p = p_board.board(i)(j)
                content.addAll(p.draw()._1, p.draw()._2, p.draw()._3)
              }
            }
        }

        def updater(): Unit = {
          draw_board()
          draw(game.board)
          //        draw(computerGame.board)
          draw_valid_moves()
          //        if(computerGame.winner() != null){
          if (game.winner() != null) {
            stage.close()
          }
        }

        /* Samo za kompjuter
        def play_computer_game() : Unit = {
          for(i <- Range(0,3)) {
            if (computerGame.turn == computerGame.computer_color) {
              play_computer_move(computerGame)
              updater()
            }

            onMousePressed = (me: MouseEvent) => {
              val rws = round(me.y) / SQUARE_SIZE
              val cls = round(me.x) / SQUARE_SIZE
              computerGame.select(rws.toInt, cls.toInt)
              updater()
              //            draw_valid_moves()
            }

          }
        }
        */

        updater()
        onMousePressed = (me: MouseEvent) => {
          val rws = round(me.y) / SQUARE_SIZE
          val cls = round(me.x) / SQUARE_SIZE
          game.select(rws.toInt, cls.toInt)
          updater()
        }

        //       play_computer_game()

      }
    } // END OF SCENE
  }

  class Board(var board: Array[Array[Piece]], var player1_left: Int, var player2_left: Int, var player1_kings: Int, var player2_kings: Int) {
    def this() {
      this(Array.ofDim[Piece](ROWS, COLS), 12, 12, 0, 0)
      this.create_board()
    }

    def setP1left(n: Int): Unit = {
      this.player1_left = n
    }

    def setP2left(n: Int): Unit = {
      this.player2_left = n
    }

    def setP1kings(n: Int): Unit = {
      this.player1_kings = n
    }

    def setP2kings(n: Int): Unit = {
      this.player2_kings = n
    }

    def getP1left: Int = {
      this.player1_left
    }

    def getP2left: Int = {
      this.player2_left
    }

    def getP1kings: Int = {
      this.player1_kings
    }

    def getP2kings: Int = {
      this.player2_kings
    }

    def get_piece(i: Int, j: Int): Piece = {
      if (this.board(i)(j) == null)
        return null
      this.board(i)(j)
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
            else if (i > 4) {
              this.board(i)(j) = new Piece(i, j, PIECE_COL2)
            }
            else
              this.board(i)(j) = null
          }
          else
            this.board(i)(j) = null
        }
    }

    def winner(): Boolean = {
      ender
    }

    def remove(pieces: List[Piece]): Unit = {
      for (p <- pieces) {
        this.board(p.row)(p.col) = null
        if (p != null) {
          if (p.getColor() == PIECE_COL1)
            this.player1_left -= 1
          else
            this.player2_left -= 1
        }
      }
    }

    private var moves = Array.ofDim[Tuple2[Boolean, List[Piece]]](ROWS * COLS)
    private var captures: Boolean = false
    private var ender = false

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
              var map = this.get_valid_moves(tmp)
              for (k <- Range(0, map.length)) {
                if (map(k)._1 && !map(k)._2.isEmpty)
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
      if (piece.king == true) {
        traverse_left(piece, List(), -step)
        traverse_right(piece, List(), -step)
      }
      //Ukoliko zelite da iskljucite obavezno preskakanje, izbrisite sledece grananje
      if (captures == true) {
        for (i <- Range(0, moves.length)) {
          if (moves(i)._1 && moves(i)._2.isEmpty)
            moves(i) = (false, List())
        }
      }
      return moves
    }

    def traverse_left(piece: Piece, skipped: List[Piece], step: Int): Unit = {
      //      if(piece == null)
      //        return
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
            if (!skipped.isEmpty && last.isEmpty) {
              break
            }
            else if (!skipped.isEmpty) {
              moves(r * ROWS + left) = (true, skipped ++ last)
            } else
              moves(r * ROWS + left) = (true, last)
            if (!last.isEmpty) {
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
      //      if(piece == null)
      //        return
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
            if (!skipped.isEmpty && last.isEmpty) {
              break
            }
            else if (!skipped.isEmpty) {
              moves(r * ROWS + right) = (true, skipped ++ last)
            } else
              moves(r * ROWS + right) = (true, last)
            if (!last.isEmpty) {
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

  class Piece(var row: Int, var col: Int, var x: Double, var y: Double, var king: Boolean, var color: Color) {
    //    def apply(row: Nothing, col: Nothing, i: Int, i1: Int, bool: Boolean, color: Nothing): Unit = ???

    def this(row: Int, col: Int, color: Color) {
      this(row, col, 0.0, 0.0, false, color)
      this.calc_positions()
    }

    def this(row: Int, col: Int, color: Color, king: Boolean) {
      this(row, col, 0.0, 0.0, king, color)
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
      if (tmp == null && this.selected.color == turn && valid_moves(row * ROWS + col)._1 == true) {
        this.board.move(this.selected, row, col)
        //treba ukloniti figurice koje su eventualno pojedene
        if (valid_moves(row * ROWS + col)._2.isEmpty == false) {
          this.board.remove(valid_moves(row * ROWS + col)._2)
        }
        this.change_turn()
        return true
      }
      return false
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

    def winner(): String = {
      if (this.board.winner()) {
        if (this.turn == PIECE_COL1)
          return "2"
        else
          return "1"
      }
      return null
    }
  } // END OF CLASS GAME

  case class ComputerGame(computer_color: Color) extends Game() {

    //    def copy() : ComputerGame = new ComputerGame(this.computer_color, (null, -1, -1, List()))

    private var lastMove: Tuple4[Piece, Int, Int, List[Piece]] = (null, -1, -1, List())

    def setLastMove(piece: Piece, row: Int, col: Int, list: List[Piece]): Unit = {
      this.lastMove = (piece, row, col, list)
    }

    def getLastMove(): Tuple4[Piece, Int, Int, List[Piece]] = lastMove

    override def select(row: Int, col: Int): Unit = {
      if (this.selected != null) {
        if (this.selected.color == computer_color)
          return
        val result = this.move(row, col)
        this.selected = null
        if (!result)
          this.select(row, col)
      }
      val piece = this.board.get_piece(row, col)
      if (piece != null && this.turn == piece.color && this.computer_color != piece.color) {
        this.selected = piece
        //uzmi odgovarajuce poteze za selektovanu figuricu
        valid_moves = this.board.get_valid_moves(this.selected)
        return
      }
    }

    //    override def


  }

  /*END OF CLASS COMPUTERGAME*/

  def play_computer_move(game: ComputerGame): Unit = {
    //    game.board.move(game.board.get_piece(0,1), 4, 3)
    val best_move_evaluated = max(game, 4, game.computer_color, 0, 0)
    val best_state = best_move_evaluated._1
    game.board.move(best_state.getLastMove()._1, best_state.getLastMove()._2, best_state.getLastMove()._3)
    if (best_state.getLastMove()._4.nonEmpty)
      game.board.remove(best_state.getLastMove()._4)
    game.change_turn()
    game.setLastMove(best_state.getLastMove()._1, best_state.getLastMove()._2, best_state.getLastMove()._3, best_state.getLastMove()._4)
  }

  def max(game: ComputerGame, depth: Int, color: Color, alpha: Int, beta: Int): Tuple2[ComputerGame, Int] = {
    if (depth == 0 || game.winner() != null)
      return (game, evaluation(game))
    var current_max = Integer.MIN_VALUE
    var current_best: ComputerGame = null
    var other_color = PIECE_COL1
    if (other_color == color)
      other_color = PIECE_COL2
    var tuple: Tuple2[ComputerGame, Int] = (null, 0)
    for (state <- get_next_states(game)) {
      tuple = min(state, depth - 1, other_color, alpha, beta)
      if (tuple._2 >= current_max) {
        current_max = tuple._2
        current_best = tuple._1
      }
    }
    (current_best, current_max)
  }

  def min(game: ComputerGame, depth: Int, color: Color, alpha: Int, beta: Int): Tuple2[ComputerGame, Int] = {
    if (depth == 0 || game.winner() != null)
      return (game, evaluation(game))
    var current_min = Integer.MAX_VALUE
    var current_best: ComputerGame = null
    var other_color = PIECE_COL1
    if (other_color == color)
      other_color = PIECE_COL2
    var tuple: Tuple2[ComputerGame, Int] = (null, 0)
    for (state <- get_next_states(game)) {
      tuple = max(state, depth - 1, other_color, alpha, beta)
      if (tuple._2 <= current_min) {
        current_min = tuple._2
        current_best = tuple._1
      }
    }

    (current_best, current_min)
  }

  def get_next_states(game: ComputerGame): List[ComputerGame] = {
    var list: List[ComputerGame] = List()
    for (i <- Range(0, ROWS))
      for (j <- Range(0, COLS)) {
        breakable {
          val piece = game.board.get_piece(i, j)
          if (piece == null)
            break
          if (piece.color == game.turn) {
            val map = game.board.get_valid_moves(piece)
            for (k <- Range(0, map.length)) {
              if (map(k)._1) {
                var temp_game = game.copy()
                temp_game.board.move(temp_game.board.get_piece(i, j), k / ROWS, k % ROWS)
                if (map(k)._2.nonEmpty) {
                  temp_game.board.remove(map(k)._2)
                }
                temp_game.change_turn()
                temp_game.setLastMove(piece, k / ROWS, k % ROWS, map(k)._2)
                list = list ++ List(temp_game)
              }
            }
          }
        }
      }
    return list
  }

  def evaluation(game: ComputerGame): Int = {
    if (game.winner() == "1" && game.computer_color == PIECE_COL1)
      return 1000
    if (game.winner() == "2" && game.computer_color == PIECE_COL2)
      return 1000
    if (game.winner() == "1" && game.computer_color == PIECE_COL2)
      return -1000
    if (game.winner() == "2" && game.computer_color == PIECE_COL1)
      return -1000
    var coef = -1
    if (game.computer_color == PIECE_COL1)
      coef = 1
    return coef * (game.board.getP1kings * 10 + game.board.getP1left - game.board.getP2kings * 10 - game.board.getP2left)
  }


  object Hint extends Game {
    var number = 3

    override def select(row: Int, col: Int): Unit = {
      if (board.get_piece(row, col) == null) {
        val piece = new Piece(row, col, turn)
        board.move(piece, row, col)
        number = number - 1
      }
    }
  }

} //END OF APP

