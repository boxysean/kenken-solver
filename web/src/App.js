import React from 'react';
import './App.css';

import Board from './components/Board';
import Modal from './components/Modal';
import SubmitButton from './components/SubmitButton';

import update from 'immutability-helper';

class App extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      modal: false,
      selected: new Set(),
      selecting: false,
      constraints: {},
      answers: [],
    };
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

  processModal(value, operator) {
    var result = Array.from(this.state.selected).reduce(function(obj, x) {
      obj[x] = {$set: value + operator};
      return obj;
    }, {});

    this.setState({
      constraints: update(this.state.constraints, result),
    });

    this.resetModal();
  }

  submit() {
    fetch("/solve", {
      method: "post",
      body: JSON.stringify({
        constraintString: "a=3+ b=6+ c=5+ d=4+",
        boardStrings: [
          "a a b",
          "c d b",
          "c d b"
        ],
      })
    })
      .then(res => res.json())
      .then((data) => {
        console.log(data.boardOutput);
        this.setState({
          answers: data.boardOutput.split(/\s/)
        });
      })
      .catch(console.log);
  }

  render() {
    return (
      <div className="App" onMouseUp={(event) => this.processRelease(event)}>
        {this.state.modal && <Modal processModal={this.processModal.bind(this)} closeModal={this.resetModal.bind(this)}></Modal>}
        <Board
          size="3"
          processHover={this.processHover.bind(this)}
          processBegin={this.processBegin.bind(this)}
          selectedCells={this.state.selected}
          constraints={this.state.constraints}
          answers={this.state.answers}
        ></Board>
        <SubmitButton onSubmit={this.submit.bind(this)}></SubmitButton>
      </div>
    );
  }
}

export default App;
