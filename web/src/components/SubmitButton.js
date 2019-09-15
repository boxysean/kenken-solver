import React from 'react';
import './SubmitButton.css';

class SubmitButton extends React.Component {
  render() {
    return (
      <button onClick={this.props.onSubmit}>
        Solve!
      </button>
    );
  }
}

export default SubmitButton;
