import React from 'react';
import {Helmet} from 'react-helmet';
import _ from 'lodash';
import './App.css';

import Board from './components/Board';
import BoardSlider from './components/BoardSlider';
import Modal from './components/Modal';
import SubmitButton from './components/SubmitButton';

import update from 'immutability-helper';

class App extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      boardSize: 5,
      modal: false,
      selected: new Set(),
      selecting: false,
      cellToConstraint: {},
      constraintCharToFormula: {},
      answers: [],
    };
  }

  componentDidMount() {
    document.addEventListener("keydown", this.escFunction.bind(this), false);
  }

  componentWillUnmount() {
    document.removeEventListener("keydown", this.escFunction.bind(this), false);
  }

  reset() {
    this.setState({
      modal: false,
      selected: new Set(),
      selecting: false,
      cellToConstraint: {},
      constraintCharToFormula: {},
      answers: [],
    });
  }

  processBegin(cellIndex) {
    this.setState({
      selected: new Set([cellIndex]),
      selecting: true,
    });
  }

  processHover(cellIndex) {
    if (this.state.selecting) {
      this.setState({
        selected: this.state.selected.add(cellIndex),
      });
    }
  }

  processRelease(cellIndex) {
    if (!this.state.modal && this.state.selecting) {
      this.setState({
        modal: true,
      });
    }
  }

  resetModal() {
    this.setState({
      modal: false,
      selected: new Set(),
      selecting: false,
    });
  }

  escFunction(event) {
    if (event.keyCode === 27) {
      this.resetModal();
    }
  }

  processModal(value, operator) {
    var nextConstraintChar = String.fromCharCode(1 +
      Object.values(this.state.cellToConstraint)
        .map((constraint) => constraint.name.charCodeAt(0))
        .reduce(
          (accumulator, constraintName) => Math.max(accumulator, constraintName),
          'a'.charCodeAt(0) - 1
        )
    );

    var formula = value + operator;

    this.setState({
      cellToConstraint: update(
        this.state.cellToConstraint,
        Array.from(this.state.selected)
          .reduce(function(obj, x) {
            obj[x] = {$set: {"name": nextConstraintChar, "formula": formula}};
            return obj;
          }, {})
      ),
      constraintCharToFormula: update(
        this.state.constraintCharToFormula,
        {[nextConstraintChar]: {$set: formula}}
      )
    });

    this.resetModal();
  }

  submit() {
    console.log(this.state.cellToConstraint);
    console.log(this.state.constraintCharToFormula);

    var constraintString = _.uniq(
      Object.values(this.state.cellToConstraint)
        .map((constraint) => constraint.name + "=" + constraint.formula)
      ).join(" ");

    var boardStrings = _.chunk(
      Object.values(this.state.cellToConstraint).map((constraint) => constraint.name),
      this.state.boardSize
    ).map((chunk) => chunk.join(" "));

    console.log("SUBMITTING");
    console.log(constraintString);
    console.log(boardStrings);

    fetch("https://api.kenken.gg/solve", {
      method: "post",
      body: JSON.stringify({
        constraintString: constraintString,
        boardStrings: boardStrings,
      })
    })
      .then(res => res.json())
      .then((data) => {
        console.log(data.boardOutput);
        this.setState({
          answers: data.boardOutput.split(/\s/),
          resultMessage: "Success!",
          resultColor: "#00ff00",
        });
      })
      .catch(error => {
        this.setState({
          resultMessage: "Fail solving! :-(",
          resultColor: "#ff0000",
        });
        console.log(error);
      });
  }

  changeBoardSize(size) {
    this.setState({
      boardSize: size,
    });

    this.reset();
  }

  processTouchMove(event) {
    // This handles touches. The onTouchMove handler is not called by the cells over which the pointer is on, only the cell it originated from.
    const touch = event.targetTouches[0];
    var cell = document.elementFromPoint(touch.clientX, touch.clientY);
    var id = parseInt(cell.id.replace("cell-", ""));
    this.processHover(id);
  }

  render() {
    return (
      <div
        className="App"
        onMouseUp={(event) => this.processRelease(event)}
        onTouchMove={(event) => this.processTouchMove(event)}
        onTouchEnd={(event) => this.processRelease(event)}
      >
        <Helmet>
          <title>kenken.gg solver</title>
        </Helmet>

        <h1 className="Title">kenken.gg solver</h1>
        <BoardSlider
          boardSize={this.state.boardSize}
          onChange={this.changeBoardSize.bind(this)}
        ></BoardSlider>

        {this.state.modal && <Modal processModal={this.processModal.bind(this)} closeModal={this.resetModal.bind(this)}></Modal>}

        <Board
          size={this.state.boardSize}
          processHover={this.processHover.bind(this)}
          processBegin={this.processBegin.bind(this)}
          selectedCells={this.state.selected}
          constraints={this.state.cellToConstraint}
          answers={this.state.answers}
        ></Board>
        <SubmitButton onSubmit={this.submit.bind(this)}></SubmitButton>
        <p style={{color: this.state.resultColor}}>{this.state.resultMessage}</p>
        <p>Aboot | GitHub | Contact</p>
      </div>
    );
  }
}

export default App;
