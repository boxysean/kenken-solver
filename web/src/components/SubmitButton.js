import React from 'react';
import './SubmitButton.css';

class SubmitButton extends React.Component {
  render() {
    return (
      <button onClick={this.props.onSubmit}>
        Submit
      </button>
    );
  }
}

export default SubmitButton;
